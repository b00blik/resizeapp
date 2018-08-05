package ru.b00blik.resizeapp.event

import javafx.event.{Event, EventTarget, EventType}

object JFXPathChangedEvent {
  // Our ANY has EventType.ROOT as a parent, others use parent to create logical
  // groupings of event types.
  val ANY: EventType[JFXPathChangedEvent] = new EventType[JFXPathChangedEvent]("PCE_ANY")
}

/**
  * This event class acts as our delegate in the scalafx api as it extends the
  * javafx classes directly and can be wrapped the same as other java objects.
  */
class JFXPathChangedEvent(
                     source: Any,
                     target: EventTarget,
                     eventType: EventType[_ <: JFXPathChangedEvent])
  extends Event(source, target, eventType) {

  def this(eventType: EventType[_ <: JFXPathChangedEvent]) {
    this(null, null, eventType)
  }
}