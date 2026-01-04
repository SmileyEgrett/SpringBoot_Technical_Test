package com.example.fileproc.domain;

import java.util.Set;

public class IpBlockingPolicy {

    private final Set<String> blockedCountryCodes;
    private final Set<String> blockedIspKeywords;

    public IpBlockingPolicy(Set<String> blockedCountryCodes, Set<String> blockedIspKeywords) {
        this.blockedCountryCodes = blockedCountryCodes;
        this.blockedIspKeywords = blockedIspKeywords;
    }

    public BlockingResult evaluate(IpInfo ipInfo) {
        if (ipInfo == null) {
            return BlockingResult.deny("Unable to validate IP: no information available");
        }

        if (!ipInfo.isSuccess()) {
            return BlockingResult.deny("Unable to validate IP: " + ipInfo.message());
        }

        String countryCode = ipInfo.countryCode();
        if (countryCode != null && blockedCountryCodes.contains(countryCode.toUpperCase())) {
            return BlockingResult.deny("Blocked country: " + countryCode);
        }

        String isp = ipInfo.isp();
        if (isp != null) {
            String ispUpper = isp.toUpperCase();
            for (String keyword : blockedIspKeywords) {
                if (ispUpper.contains(keyword)) {
                    return BlockingResult.deny("Blocked ISP: " + isp);
                }
            }
        }

        return BlockingResult.allow();
    }
}
