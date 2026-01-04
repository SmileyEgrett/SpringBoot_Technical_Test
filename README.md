# File Processing API

A Spring Boot application that processes entry files and produces outcome files in JSON format.

## Requirements

- Java 17+
- Maven 3.6+

## Running the Application

### Local Development (Recommended)

For local development, run with the `dev` profile to disable IP blocking:

```bash
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

Then test with:

```bash
curl -X POST http://localhost:8080/api/v1/outcome-file -F "file=@EntryFile.txt" -o OutcomeFile.json
```

### Production Mode

Without the dev profile, IP blocking is enabled:

```bash
./mvnw spring-boot:run
```

To test with a specific IP (requires `ip-blocking.trust-proxy-headers=true`):

```bash
curl -X POST http://localhost:8080/api/v1/outcome-file -H "X-Forwarded-For: 8.8.8.8" -F "file=@EntryFile.txt" -o OutcomeFile.json
```

The application starts on port 8080 by default.

## Running Tests

```bash
./mvnw test
```

## API Usage

### Process File

**Endpoint:** `POST /api/v1/outcome-file`

**Content-Type:** `multipart/form-data`

**Input File Format (EntryFile.txt):**

Files must be UTF-8 encoded.

```
UUID|ID|Name|Likes|Transport|AvgSpeed|TopSpeed
```

**Output (OutcomeFile.json):**
```json
[
  {"name": "John Smith", "transport": "Rides A Bike", "topSpeed": 12.1}
]
```

## Configuration

### Feature Flag: Skip Validation

To disable strict file validation, set in `application.properties`:

```properties
features.fileValidation.enabled=false
```

When disabled, the API only requires valid Name, Transport, and TopSpeed fields. UUID and AvgSpeed are not validated.

Default is `true` (strict validation enabled).

### IP Blocking

The application blocks requests from:
- Countries: China (CN), Spain (ES), USA (US)
- ISPs: AWS, GCP, Azure

Blocked requests return HTTP 403 with a reason.

**Configuration (application.properties):**

```properties
# Enable/disable IP blocking entirely (default: true)
ip-blocking.enabled=true

# Blocked country codes (comma-separated)
ip-blocking.blocked-countries=CN,ES,US

# Blocked ISP keywords (comma-separated, case-insensitive)
ip-blocking.blocked-isps=AWS,GCP,AZURE

# Trust X-Forwarded-For header for client IP resolution (default: false)
ip-blocking.trust-proxy-headers=false
```

### Client IP Resolution

The client IP is determined by:
1. `X-Forwarded-For` header (if `ip-blocking.trust-proxy-headers=true`)
2. `request.getRemoteAddr()` (fallback)

**Security Note:** Only enable `trust-proxy-headers` when running behind a trusted reverse proxy.

## Database

Uses H2 in-memory database for request audit logging.
