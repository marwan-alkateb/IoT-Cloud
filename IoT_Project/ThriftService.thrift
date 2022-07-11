
/** constructs an interface to implement the CRUD methods */
service ThriftService {
    bool                createEntry     (1: SensorData sensorData)
    map<string,string>  readEntry       (1: string received_ID)
    string              updateEntry     (1: string received_ID , 2: map<string,string> newEntry)
    string              deleteEntry     (1: string received_ID)
}

/** Sensor Data type describes the data stream that comes from the sensors
 * This is to follow the “Separate of Concerns” principle.
 **/
struct SensorData {
   1: string sensorType,
   2: string temperatureValue,
   3: string humidityValue,
   4: string fluidValue
}

/* REMARKS:
 The Service can be generated as a java file using the command :
 sudo thrift --gen java [filename].thrift
 in our case :
 sudo thrift --gen java ThriftService.thrift
 */
