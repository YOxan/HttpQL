package com.yoxan.astraeus.util.ssl

import java.security.cert.X509Certificate

import javax.net.ssl.X509TrustManager

object TrustAllManagers extends X509TrustManager {
  override def checkClientTrusted(chain: Array[X509Certificate], authType: String): Unit = Unit

  override def checkServerTrusted(chain: Array[X509Certificate], authType: String): Unit = Unit

  override def getAcceptedIssuers: Array[X509Certificate] =
    null
}
