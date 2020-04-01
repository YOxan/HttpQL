package com.yoxan.astraeus.util.ssl

import java.io.{ BufferedInputStream, File, FileInputStream, InputStream }
import java.security.KeyStore

import cats.effect.Sync
import cats.implicits._
import javax.net.ssl.{ KeyManager, SSLContext }

object SSLContextBuilder {

  private def getKeystore[F[_]: Sync](javaKeyStoreStream: InputStream, keyStorePass: String): F[KeyStore] =
    Sync[F].delay {
      val keyStore = KeyStore.getInstance("JKS")
      keyStore.load(javaKeyStoreStream, keyStorePass.toCharArray)
      keyStore
    }

  private def getSSLContext[F[_]: Sync](keyStore: KeyStore): F[SSLContext] = Sync[F].delay {
    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(Array.empty[KeyManager], Array(TrustAllManagers), null)
    sslContext
  }

  def build[F[_]: Sync](
      javaKeystoreIS: InputStream,
      keyStorePass: String
  ): F[SSLContext] =
    getKeystore[F](javaKeystoreIS, keyStorePass).flatMap(getSSLContext[F](_))

  def build[F[_]: Sync](javaKeystoreFile: File, keyStorePass: String): F[SSLContext] =
    Sync[F]
      .delay(new BufferedInputStream(new FileInputStream(javaKeystoreFile)))
      .flatMap(build(_, keyStorePass))
}
