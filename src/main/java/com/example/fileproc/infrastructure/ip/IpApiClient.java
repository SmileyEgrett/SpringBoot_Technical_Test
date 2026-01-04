package com.example.fileproc.infrastructure.ip;

import com.example.fileproc.domain.IpInfo;

public interface IpApiClient {

    IpInfo lookup(String ip);
}
