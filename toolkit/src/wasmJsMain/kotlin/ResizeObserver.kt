@file:Suppress("unused") // external

package quest.laxla.spock.toolkit

import org.w3c.dom.Element

internal external class ResizeObserver(callback: (entries: JsArray<ResizeObserverEntry>, observer: ResizeObserver) -> Unit) : JsAny {
	fun disconnect()
	fun observe(element: Element, options: ResizeObserverOptions = definedExternally)
	fun unobserve(element: Element, options: ResizeObserverOptions = definedExternally)
}

internal external interface BoxSize : JsAny {
	val blockSize: Int
	val inlineSize: Int
}

internal external interface ResizeObserverEntry : JsAny {
	val borderBoxSize: JsArray<BoxSize>?
	val contentBoxSize: JsArray<BoxSize>?
	val devicePixelContentBoxSize: JsArray<BoxSize>?
	val target: Element
}

internal external interface ResizeObserverOptions : JsAny {
	var box: String
}

internal const val ContentBox = "content-box"
internal const val BorderBox = "border-box"
internal const val DevicePixelContentBox = "device-pixel-content-box"

//language=javascript
internal fun ResizeObserverOptions(box: String = ContentBox): ResizeObserverOptions = js("({ box: box })")
