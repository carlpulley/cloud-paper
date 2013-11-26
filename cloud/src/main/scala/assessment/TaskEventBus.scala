// Copyright (C) 2013  Carl Pulley
// 
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <http://www.gnu.org/licenses/>.

package cloud.assessment

import akka.event.EventBus
import akka.event.SubchannelClassification
import akka.util.Subclassification
import rx.lang.scala.Observer
import scalaz._
import scalaz.camel.core._

case class TaskEvent(channel: String, message: Message)

object TaskEventBus extends EventBus with SubchannelClassification {
  type Event = TaskEvent
  type Classifier = String
  type Subscriber = Observer[Message]

  protected def classify(event: Event): Classifier = event.channel

  protected def subclassification = new Subclassification[Classifier] {
    def isEqual(x: Classifier, y: Classifier) = x == y
    def isSubclass(x: Classifier, y: Classifier) = x.startsWith(y)
  }

  protected def publish(event: Event, subscriber: Subscriber) {
    subscriber.onNext(event.message)
  }
}
