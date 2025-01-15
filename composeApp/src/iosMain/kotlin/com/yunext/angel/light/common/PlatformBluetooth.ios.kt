//package com.yunext.angel.light.common
//
//import platform.CoreBluetooth.CBCentralManager
//import platform.CoreBluetooth.CBCentralManagerDelegateProtocol
//import platform.CoreBluetooth.CBPeripheral
//import platform.darwin.NSObject
//
//
//// iOS 特定代码
// class BluetoothManager : NSObject(), CBCentralManagerDelegateProtocol, CBCC {
//    private var centralManager: CBCentralManager? = null
//    private var discoveredPeripherals: MutableList<CBPeripheral> = mutableListOf()
//
//    init {
//        centralManager = CBCentralManager(delegate = this, queue = null)
//    }
//
//     fun startScanning() {
//        centralManager?.scanForPeripheralsWithServices(emptyList(), emptyMap())
//    }
//
//     fun stopScanning() {
//        centralManager?.stopScan()
//    }
//
//    // CBCentralManagerDelegate methods
//    override fun centralManagerDidUpdateState(central: CBCentralManager) {
//        if central.state == .poweredOn {
//            // Start scanning if Bluetooth is powered on
//            startScanning()
//        }
//    }
//
//    override fun centralManager(central: CBCentralManager, didDiscoverPeripheral peripheral: CBPeripheral, advertisementData: [String : Any], rssi RSSI: NSNumber) {
//        // Add discovered peripheral to list
//        discoveredPeripherals.add(peripheral)
//    }
//}
