package quest.laxla.spock.toolkit

import com.sun.jna.Pointer
import org.rococoa.ID
import org.rococoa.ObjCObject
import org.rococoa.Rococoa
import ffi.NativeAddress
import java.lang.foreign.MemorySegment

internal fun Long.toNativeAddress(): NativeAddress = NativeAddress(MemorySegment.ofAddress(this))
internal fun Pointer.toNativeAddress(): NativeAddress = Pointer.nativeValue(this).toNativeAddress()
internal inline fun <reified T> ID.rococoa() where T : ObjCObject = Rococoa.wrap(this, T::class.java)
internal fun Long.toRococoaPointer(): ID = ID.fromLong(this)
internal inline fun <reified T> Long.rococoa() where T : ObjCObject = toRococoaPointer().rococoa<T>()
internal fun Long.toPointer(): Pointer = Pointer(this)
