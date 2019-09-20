package com.yoxan.astraeus.util

import java.io.{ BufferedInputStream, File, FileInputStream, InputStream }
import java.security.KeyStore

import cats.effect.Sync
import cats.syntax.flatMap._
import javax.net.ssl.{ KeyManagerFactory, SSLContext }

object SSLContextBuilder {

  private def getKeystore[F[_]: Sync](javaKeyStoreStream: InputStream, keyStorePass: String): F[KeyStore] =
    Sync[F].delay {
      val keyStore = KeyStore.getInstance("JKS")
      keyStore.load(javaKeyStoreStream, keyStorePass.toCharArray)
      keyStore
    }

  private def getSSLContext[F[_]: Sync](keyStore: KeyStore, keyManagerPassword: String): F[SSLContext] = Sync[F].delay {
    val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm)
    val sslContext        = SSLContext.getInstance("TLS")

    keyManagerFactory.init(keyStore, keyManagerPassword.toCharArray)
    sslContext.init(keyManagerFactory.getKeyManagers, null, null)

    sslContext
  }

  def build[F[_]: Sync](javaKeystoreIS: InputStream, keyStorePass: String, keyManagerPass: String): F[SSLContext] =
    getKeystore(javaKeystoreIS, keyStorePass).flatMap(
      getSSLContext(_, keyManagerPass)
    )

  def build[F[_]: Sync](javaKeystoreFile: File, keyStorePass: String, keyManagerPass: String): F[SSLContext] =
    Sync[F]
      .delay(new BufferedInputStream(new FileInputStream(javaKeystoreFile)))
      .flatMap(build(_, keyStorePass, keyManagerPass))
}
