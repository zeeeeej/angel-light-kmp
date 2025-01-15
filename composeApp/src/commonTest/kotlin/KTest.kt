import androidx.compose.runtime.TestOnly
import com.yunext.angel.light.repository.ble.SetDeviceInfoKey
import com.yunext.angel.light.repository.ble.key
import kotlin.test.Test

class KTest {

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun t1(){
        val data = "0200".hexToByteArray()
        val a = data[0]//0x02.toByte()
        println( SetDeviceInfoKey.Set2.key == a)
        when(a){
            SetDeviceInfoKey.Set2.key-> println("ok")
            else-> println("error")
        }
    }
}