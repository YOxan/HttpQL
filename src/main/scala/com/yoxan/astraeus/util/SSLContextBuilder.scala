package com.yoxan.astraeus.util

import java.io.{ BufferedInputStream, File, FileInputStream }
import java.security.KeyStore

import cats.effect.Sync
import cats.syntax.flatMap._
import javax.net.ssl.{ KeyManagerFactory, SSLContext }

object SSLContextBuilder {
  private def getKeystore[F[_]: Sync](javaKeyStoreFile: File, keyStorePass: String): F[KeyStore] = Sync[F].delay {
    val keystoreReader = new BufferedInputStream(new FileInputStream(javaKeyStoreFile))
    val keyStore       = KeyStore.getInstance("JKS")

    keyStore.load(keystoreReader, keyStorePass.toCharArray)

    keyStore
  }

  private def getSSLContext[F[_]: Sync](keyStore: KeyStore, keyManagerPassword: String): F[SSLContext] = Sync[F].delay {
    val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm)
    val sslContext        = SSLContext.getInstance("TLS")

    keyManagerFactory.init(keyStore, keyManagerPassword.toCharArray)
    sslContext.init(keyManagerFactory.getKeyManagers, null, null)

    sslContext
  }

  def build[F[_]: Sync](javaKeystoreFile: File, keyStorePass: String, keyManagerPass: String): F[SSLContext] =
    getKeystore(javaKeystoreFile, keyStorePass).flatMap(
      getSSLContext(_, keyManagerPass)
    )
}
