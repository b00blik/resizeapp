package ru.b00blik.resizeapp.event

import javafx.{event => jfxe}
import scalafx.Includes._
import scalafx.delegate.SFXDelegate
import scalafx.event.{Event, EventType}

import scala.language.implicitConversions


class PathChangedEvent(override val delegate: JFXPathChangedEvent) extends Event(delegate) with SFXDelegate[JFXPathChangedEvent]{

  def this(eventType: EventType[_ <: JFXPathChangedEvent]) = this(new JFXPathChangedEvent(eventType))

  def this(source: Any, target: jfxe.EventTarget, eventType: EventType[_ <: JFXPathChangedEvent]) =
    this(new JFXPathChangedEvent(source, target, eventType))
}

object PathChangedEvent {
    implicit def sfxEvent2jfx(e: PathChangedEvent): jfxe.Event = if (e != null) e.delegate else null
    implicit def jsx2sfx = (e: JFXPathChangedEvent) => new PathChangedEvent(e)


    def apply[T <: JFXPathChangedEvent](eventType: jfxe.EventType[T]) = new PathChangedEvent(new JFXPathChangedEvent(eventType))

    def fireEvent(eventTarget: jfxe.EventTarget, event: jfxe.Event) {
      jfxe.Event.fireEvent(eventTarget, event)
    }

    val NULL_SOURCE_TARGET: jfxe.EventTarget = jfxe.Event.NULL_SOURCE_TARGET
    val Any: EventType[JFXPathChangedEvent] = JFXPathChangedEvent.ANY
  
}
