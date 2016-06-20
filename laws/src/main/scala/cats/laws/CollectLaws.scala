package cats
package laws

import cats.data.Nested
import cats.implicits._

trait CollectLaws[F[_]] extends TraverseLaws[F] {
  implicit override def F: Collect[F]

  def mapOptionAIdentity[G[_]:Applicative, A](fa: F[A]): IsEq[G[F[A]]] = {
    fa.mapOptionA(_.some.pure[G]) <-> fa.pure[G]
  }

  def mapOptionAComposition[A, B, C, M[_], N[_]](
    fa: F[A],
    f: A => M[Option[B]],
    g: B => N[Option[C]]
  )(implicit
      M: Applicative[M],
      N: Applicative[N]
  ): IsEq[Nested[M, N, F[C]]] = {

    val lhs: Nested[M, N, F[C]] = Nested(fa.mapOptionA(f).map(_.mapOptionA(g)))
    val rhs: Nested[M, N, F[C]] = fa.mapOptionA[Nested[M, N, ?], C](a =>
      Nested(f(a).map(_.mapOptionA(g))))
    lhs <-> rhs
  }
}

object CollectLaws {
  def apply[F[_]](implicit ev: Collect[F]): CollectLaws[F] =
    new CollectLaws[F] { def F: Collect[F] = ev }
}
