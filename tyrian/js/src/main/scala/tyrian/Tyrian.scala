package tyrian

import cats.effect.kernel.Async
import org.scalajs.dom.Element
import tyrian.runtime.RunWithCallback
import tyrian.runtime.TyrianRuntime
import tyrian.runtime.TyrianSSR

object Tyrian:

  type Event            = org.scalajs.dom.Event
  type KeyboardEvent    = org.scalajs.dom.KeyboardEvent
  type HTMLInputElement = org.scalajs.dom.raw.HTMLInputElement

  /** Directly starts the app. Computes the initial state of the given application, renders it on the given DOM element,
    * and listens to user actions
    * @param init
    *   initial state
    * @param update
    *   state transition function
    * @param view
    *   view function
    * @param subscriptions
    *   subscriptions function
    * @param node
    *   the DOM element to mount the app to
    * @param runner
    *   the function that runs the program. Has a type of `F[Option[Msg]] => (Either[Throwable, Option[Msg]] => Unit) =>
    *   Unit`, essentially: `task.unsafeRunAsync(callback)`
    * @tparam F
    *   The effect type to use, e.g. `IO`
    * @tparam Model
    *   Type of model
    * @tparam Msg
    *   Type of messages
    */
  def start[F[_]: Async, Model, Msg](
      node: Element,
      init: (Model, Cmd[F, Msg]),
      update: (Msg, Model) => (Model, Cmd[F, Msg]),
      view: Model => Html[Msg],
      subscriptions: Model => Sub[F, Msg],
      runner: RunWithCallback[F, Msg]
  ): Unit =
    new TyrianRuntime(
      init,
      update,
      view,
      subscriptions,
      node,
      runner
    ).start()

  /** Takes a normal Tyrian Model and view function and renders the html to a string prefixed with the doctype.
    */
  def render[Model, Msg](includeDocType: Boolean, model: Model, view: Model => Html[Msg]): String =
    TyrianSSR.render(includeDocType, model, view)

  /** Takes a normal Tyrian Model and view function and renders the html to a string.
    */
  def render[Model, Msg](model: Model, view: Model => Html[Msg]): String =
    render(false, model, view)

  /** Takes a Tyrian HTML view, and renders it into to a string prefixed with the doctype.
    */
  def render[Model, Msg](includeDocType: Boolean, html: Html[Msg]): String =
    TyrianSSR.render(includeDocType, html)

  /** Takes a Tyrian HTML view, and renders it into to a string.
    */
  def render[Model, Msg](html: Html[Msg]): String =
    render(false, html)

  /** Takes a list of Tyrian elements, and renders the fragment into to a string prefixed with the doctype.
    */
  def render[Model, Msg](includeDocType: Boolean, elems: List[Elem[Msg]]): String =
    TyrianSSR.render(includeDocType, elems)

  /** Takes a list of Tyrian elements, and renders the fragment into to a string.
    */
  def render[Model, Msg](elems: List[Elem[Msg]]): String =
    render(false, elems)

  /** Takes repeatingTyrian elements, and renders the fragment into to a string prefixed with the doctype.
    */
  def render[Model, Msg](includeDocType: Boolean, elems: Elem[Msg]*): String =
    render(includeDocType, elems.toList)

  /** Takes repeating Tyrian elements, and renders the fragment into to a string.
    */
  def render[Model, Msg](elems: Elem[Msg]*): String =
    render(elems.toList)
