package quest.laxla.spock

import com.sun.jna.Pointer
import org.rococoa.ID
import org.rococoa.ObjCObject
import org.rococoa.Rococoa
import java.lang.foreign.MemorySegment

internal fun Long.toMemorySegment() = MemorySegment.ofAddress(this)
internal fun Pointer.toMemorySegment() = Pointer.nativeValue(this).toMemorySegment()
internal inline fun <reified T> ID.rococoa() where T : ObjCObject = Rococoa.wrap(this, T::class.java)
internal fun Long.toRococoaPointer(): ID = ID.fromLong(this)
internal inline fun <reified T> Long.rococoa() where T : ObjCObject = toRococoaPointer().rococoa<T>()
internal fun Long.toPointer(): Pointer = Pointer(this)
