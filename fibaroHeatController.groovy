/*
 * This device handler is for Fibaro heat Controller
 * It's a modified version of https://github.com/constjs/jcdevhandlers/blob/master/devicetypes/jscgs350/my-zwave-thermostat.src/my-zwave-thermostat.groovy
 * Many things are left to fix, but simple control of temperature is working at the moment
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *
 *
*/
metadata {
	// Automatically generated. Make future change here.
	definition (name: "Fibaro Heat Controller", namespace: "pettab", author: "SmartThings")
    { 
		capability "Refresh"
		capability "Actuator"
		capability "Temperature Measurement"
		capability "Relative Humidity Measurement"
        capability "Thermostat Fan Mode"
		capability "Thermostat"
		capability "Configuration"
		capability "Polling"
		capability "Sensor"
        capability "Health Check"
        capability "Switch"

		command "setLevelUp"
		command "setLevelDown"
		command "heatLevelUp"
		command "heatLevelDown"
        command "quickSetHeat"
		command "coolLevelUp"
		command "coolLevelDown"
        command "quickSetCool"
        command "offmode"
        
		attribute "thermostatFanState", "string"
        attribute "currentState", "string"
        attribute "currentMode", "string"
        attribute "currentfanMode", "string"
	}

    preferences {
        input "debugOutput", "boolean", title: "Enable debug logging?", defaultValue: false, displayDuringSetup: true
    }

//Thermostat Temp and State
	tiles(scale: 2) {
		multiAttributeTile(name:"temperature", type: "thermostat", width: 6, height: 4, decoration: "flat"){
            tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
                attributeState("temperature", label:'${currentValue}°')
            }
			tileAttribute("device.heatingSetpoint", key: "VALUE_CONTROL") {
				attributeState("VALUE_UP", action: "setLevelUp")
				attributeState("VALUE_DOWN", action: "setLevelDown")
			}           
            tileAttribute("device.thermostatOperatingState", key: "OPERATING_STATE") {
                attributeState("idle", backgroundColor:"#44b621")
                attributeState("heating", backgroundColor:"#ea5462")
				attributeState("pending heat", backgroundColor:"#B27515")
				attributeState("vent economizer", backgroundColor:"#8000FF")
            }
            tileAttribute("device.thermostatMode", key: "THERMOSTAT_MODE") {
                attributeState("off", label:'${name}')
                attributeState("heat", label:'${name}')
                attributeState("emergency heat", label:'${name}')
            }
            tileAttribute("device.heatingSetpoint", key: "HEATING_SETPOINT") {
                attributeState("default", label:'${currentValue}°')
            }
            
            tileAttribute("device.thermostatSetpoint", key: "THERMOSTAT_SETPOINT") {
                attributeState("default", label:'${currentValue}°')
            } 
		}       

//Thermostat Mode Control
        standardTile("modeheat", "device.thermostatMode", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
            state "heat", label:'Heat Mode', action:"heat", icon:"https://raw.githubusercontent.com/constjs/jcdevhandlers/master/img/heat@2x.png"
        }
        standardTile("modeauto", "device.thermostatMode", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
            state "auto", label:'Auto Mode', action:"auto", icon:"https://raw.githubusercontent.com/constjs/jcdevhandlers/master/img/auto@2x.png"
        }
        standardTile("modeheatemrgcy", "device.thermostatMode", width: 3, height: 1, inactiveLabel: false, decoration: "flat") {
            state "heatemrgcy", label:'', action:"emergencyHeat", icon:"st.thermostat.emergency-heat"
        }         
        standardTile("modeoff", "device.thermostatMode", width: 3, height: 1, inactiveLabel: false, decoration: "flat") {
            state "off", label: '', action:"offmode", icon:"st.thermostat.heating-cooling-off"
        }        

//Heating Set Point Controls
        standardTile("heatTile", "device.heatingSetpoint", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:'', icon:"st.thermostat.heat", backgroundColor: "#ee7681"
		}
		controlTile("heatSliderControl", "device.heatingSetpoint", "slider", width: 1, height: 2, inactiveLabel: false, range:"(10..30)") {
			state "default", label:'${currentValue}', action:"quickSetHeat", backgroundColor: "#bb434e"
		}



//Refresh and Config Controls
		valueTile("refresh", "device.refresh", width: 3, height: 1, inactiveLabel: false, decoration: "flat") {
			state "default", label:'Refresh', action:"polling.poll", icon:"st.secondary.refresh-icon"
		}
		valueTile("configure", "device.configure", width: 3, height: 1, inactiveLabel: false, decoration: "flat") {
			state "configure", label:'', action:"configuration.configure", icon:"st.secondary.configure"
		}

//Miscellaneous tiles used in this DH
        valueTile("statusL1Text", "statusL1Text", inactiveLabel: false, decoration: "flat", width: 3, height: 1) {
			state "default", label:'${currentValue}', icon:"st.Home.home1"
		}
        valueTile("statusL2Text", "statusL2Text", inactiveLabel: false, decoration: "flat", width: 3, height: 1) {
			state "default", label:'${currentValue}', icon:"https://raw.githubusercontent.com/constjs/jcdevhandlers/master/img/fan-on@2x.png"
		}
        valueTile("temperature2", "device.temperature", width: 1, height: 1, canChangeIcon: true) {
            state "temperature", label: '${currentValue}°', icon:"st.thermostat.ac.air-conditioning",
				backgroundColors:[
							[value: 0, color: "#153591"],
							[value: 7, color: "#1e9cbb"],
							[value: 15, color: "#90d2a7"],
							[value: 23, color: "#44b621"],
							[value: 28, color: "#f1d801"],
							[value: 35, color: "#d04e00"],
							[value: 37, color: "#bc2323"],
							// Fahrenheit
							[value: 40, color: "#153591"],
							[value: 44, color: "#1e9cbb"],
							[value: 59, color: "#90d2a7"],
							[value: 74, color: "#44b621"],
							[value: 84, color: "#f1d801"],
							[value: 95, color: "#d04e00"],
							[value: 96, color: "#bc2323"]
            ]
        }
        
		main (["temperature2"])
		details(["temperature", "heatTile", "heatSliderControl", "statusL1Text", "statusL2Text", "modeheat", "modeheatemrgcy", "refresh", "modeoff", "configure"])
	}
}

def updated(){
	state.debug = ("true" == debugOutput)
	// Device-Watch simply pings if no device events received for 32min(checkInterval)
	sendEvent(name: "checkInterval", value: 2 * 15 * 60 + 2 * 60, displayed: false, data: [protocol: "zwave", hubHardwareId: device.hub.hardwareID])
}


def parse(String description)
{
	 log.debug "raw data: ${description}"
    def result = null
	if (description == "updated") {
		result = null
	} else {
		def cmd = zwave.parse(description, [0x31: 5, 0x30: 2, 0x84: 1])
		if (cmd) {
			result = zwaveEvent(cmd)
		}
	}
	log.debug "Parsed '${description}' to ${result.inspect()}"
	return result
}

def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation cmd) {
	def encapsulatedCommand = cmd.encapsulatedCommand([0x31: 5, 0x30: 2, 0x84: 1])
	 log.debug "encapsulated: ${encapsulatedCommand}"
	if (encapsulatedCommand) {
		zwaveEvent(encapsulatedCommand)
	} else {
		log.warn "Unable to extract encapsulated cmd from $cmd"
		createEvent(descriptionText: cmd.toString())
	}
}

//
//Receive updates from the thermostat and update the app
//

def zwaveEvent(physicalgraph.zwave.commands.thermostatsetpointv2.ThermostatSetpointReport cmd)
{
	if (state.debug) log.debug "ThermostatSetPointReport...START"
	def cmdScale = cmd.scale == 1 ? "F" : "C"
    if (state.debug) log.debug "cmdScale is $cmd.scale before (this is the state variable), and $cmdScale after"
    if (state.debug) log.debug "setpoint requested is $cmd.scaledValue and unit is $cmdScale"
	def map = [:]
	map.value = convertTemperatureIfNeeded(cmd.scaledValue, cmdScale, cmd.precision)
	map.unit = getTemperatureScale()
	map.displayed = false
    if (state.debug) log.debug "value is $map.value and unit is $map.unit"
	switch (cmd.setpointType) {
		case 1:
			map.name = "heatingSetpoint"
			break;
		case 2:
			map.name = "coolingSetpoint"
			break;
		default:
			return [:]
	}
	// So we can respond with same format
	state.size = cmd.size
	state.scale = cmd.scale
	state.precision = cmd.precision
    if (state.debug) log.debug "map is $map"
    if (state.debug) log.debug "ThermostatSetPointReport...END"
	createEvent(map)
}


def zwaveEvent(physicalgraph.zwave.commands.sensormultilevelv5.SensorMultilevelReport cmd)
{
	def map = [:]
	switch (cmd.sensorType) {
		case 1:
			map.name = "temperature"
			def cmdScale = cmd.scale == 1 ? "F" : "C"
            Float value = convertTemperatureIfNeeded(cmd.scaledSensorValue, cmdScale, cmd.precision).toFloat()
            log.debug "TEMP $value"
			if (tempOffset) {
				Float offset = tempOffset.toFloat()	
				Float v = value
				value = (v + offset) as Float
			}
 		   	Float nv = Math.round( (value as Float) * 10.0 ) / 10	// Need at least one decimal point
		   	value = nv as Float
			def descriptionText = "${device.displayName} Temperature is ${value}°${temperatureScale}"
			map.value = value
			map.unit = getTemperatureScale()
			map.name = "temperature"
            map.descriptionText = descriptionText          
			break;

		default:
			map.descriptionText = cmd.toString()
	}
	createEvent(map)
}


def zwaveEvent(physicalgraph.zwave.commands.thermostatoperatingstatev1.ThermostatOperatingStateReport cmd)
{
	if (state.debug) log.debug "ThermostatOperatingStateReport...START"
	def map = [:]
	switch (cmd.operatingState) {
		case physicalgraph.zwave.commands.thermostatoperatingstatev1.ThermostatOperatingStateReport.OPERATING_STATE_IDLE:
			map.value = "idle"
            sendEvent(name: "currentState", value: "Idle" as String)
			break
		case physicalgraph.zwave.commands.thermostatoperatingstatev1.ThermostatOperatingStateReport.OPERATING_STATE_HEATING:
			map.value = "heating"
           	sendEvent(name: "currentState", value: "running" as String)
			break
		case physicalgraph.zwave.commands.thermostatoperatingstatev1.ThermostatOperatingStateReport.OPERATING_STATE_COOLING:
			map.value = "cooling"
            sendEvent(name: "currentState", value: "running" as String)
			break
		case physicalgraph.zwave.commands.thermostatoperatingstatev1.ThermostatOperatingStateReport.OPERATING_STATE_FAN_ONLY:
			map.value = "fan only"
			sendEvent(name: "currentState", value: "fan only" as String)
			break
		case physicalgraph.zwave.commands.thermostatoperatingstatev1.ThermostatOperatingStateReport.OPERATING_STATE_PENDING_HEAT:
			map.value = "pending heat"
            sendEvent(name: "currentState", value: "pending heat" as String)
			break
		case physicalgraph.zwave.commands.thermostatoperatingstatev1.ThermostatOperatingStateReport.OPERATING_STATE_PENDING_COOL:
			map.value = "pending cool"
            sendEvent(name: "currentState", value: "pending cool" as String)
			break
		case physicalgraph.zwave.commands.thermostatoperatingstatev1.ThermostatOperatingStateReport.OPERATING_STATE_VENT_ECONOMIZER:
			map.value = "vent economizer"
            sendEvent(name: "currentState", value: "vent economizer" as String)
			break
	}
	map.name = "thermostatOperatingState"
    if (state.debug) log.debug "map is $map"
    if (state.debug) log.debug "ThermostatOperatingStateReport...END"
    
	createEvent(map)
}



def zwaveEvent(physicalgraph.zwave.commands.thermostatmodev2.ThermostatModeReport cmd) {
	if (state.debug) log.debug "ThermostatModeReport...START"
	def map = [:]
	switch (cmd.mode) {
		case physicalgraph.zwave.commands.thermostatmodev2.ThermostatModeReport.MODE_OFF:
			map.value = "off"
            sendEvent(name: "currentMode", value: "Off" as String)
            break
		case physicalgraph.zwave.commands.thermostatmodev2.ThermostatModeReport.MODE_HEAT:
			map.value = "heat"
            sendEvent(name: "currentMode", value: "Heat" as String)
            break
		case physicalgraph.zwave.commands.thermostatmodev2.ThermostatModeReport.MODE_AUXILIARY_HEAT:
			map.value = "emergencyHeat"
            sendEvent(name: "currentMode", value: "E-Heat" as String)
            break
		case physicalgraph.zwave.commands.thermostatmodev2.ThermostatModeReport.MODE_COOL:
			map.value = "cool"
            sendEvent(name: "currentMode", value: "Cool" as String)
            break
		case physicalgraph.zwave.commands.thermostatmodev2.ThermostatModeReport.MODE_AUTO:
			map.value = "auto"
            sendEvent(name: "currentMode", value: "Auto" as String)
            break
	}
	map.name = "thermostatMode"
    if (state.debug) log.debug "map is $map"
    if (state.debug) log.debug "ThermostatModeReport...END"
	
    createEvent(map)
}


def zwaveEvent(physicalgraph.zwave.commands.thermostatmodev2.ThermostatModeSupportedReport cmd) {
	def supportedModes = ""
	if(cmd.off) { supportedModes += "off " }
	if(cmd.heat) { supportedModes += "heat " }
	if(cmd.auxiliaryemergencyHeat) { supportedModes += "emergencyHeat " }
	if(cmd.cool) { supportedModes += "cool " }
	if(cmd.auto) { supportedModes += "auto " }
	state.supportedModes = supportedModes
}



def updateState(String name, String value) {
	state[name] = value
	device.updateDataValue(name, value)
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd) {
	if (state.debug) log.debug "Zwave event received: $cmd"
}


def zwaveEvent(physicalgraph.zwave.commands.schedulev1.CommandScheduleReport  cmd) {
	log.warn "schedulev1.CommandScheduleReport command $cmd"
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	log.warn "Unexpected zwave command $cmd"
}

def zwaveEvent(physicalgraph.zwave.commands.clockv1.ClockReport cmd)
{
    try
    {
        log.debug "Device clock received: weekday:${cmd.weekday} hour:${cmd.hour} minute:${cmd.minute}"
        state.clock = "${cmd.weekday},${cmd.hour}:${cmd.minute}"
        setClock()
	}
    catch (Exception ex)
    {
    	log.error "$ex"
    }
}

//
//Send commands to the thermostat
//

def setLevelUp(){
	if (state.debug) log.debug "Setting the setpoint UP a degree..."
    if (device.latestValue("thermostatMode") == "heat") {
    	if (state.debug) log.debug "...for heat..."
    	int nextLevel = device.currentValue("heatingSetpoint") + 1
    	setHeatingSetpoint(nextLevel)
	}    
}

def setLevelDown(){
	if (state.debug) log.debug "Setting the setpoint DOWN a degree..."
    if (device.latestValue("thermostatMode") == "heat") {
    	if (state.debug) log.debug "...for heat..."
    	int nextLevel = device.currentValue("heatingSetpoint") - 1
    	setHeatingSetpoint(nextLevel)
	} else if (device.latestValue("thermostatMode") == "cool") {
    	if (state.debug) log.debug "...for cool..."    
        int nextLevel = device.currentValue("coolingSetpoint") - 1
        setCoolingSetpoint(nextLevel)
	} else if (device.latestValue("thermostatMode") == "auto") {
        int nextHeatLevel = device.currentValue("heatingSetpoint") - 1
        int nextCoolLevel = device.currentValue("coolingSetpoint") - 1
        if (device.latestValue("thermostatOperatingState") == "heating") {
        	if (state.debug) log.debug "...for auto heat..."
        	setHeatingSetpoint(nextHeatLevel)
        } else if (device.latestValue("thermostatOperatingState") == "cooling") {
        	if (state.debug) log.debug "...for auto cool..."
        	setCoolingSetpoint(nextCoolLevel)
        } else {
            if (state.debug) log.debug "...for auto heat AND cool..."
	    	delayBetween([setHeatingSetpoint(nextHeatLevel), setCoolingSetpoint(nextCoolLevel)], 3000)
        }
	}    
}

def heatLevelUp(){
    int nextLevel = device.currentValue("heatingSetpoint") + 1
    setHeatingSetpoint(nextLevel)
}

def heatLevelDown(){
    int nextLevel = device.currentValue("heatingSetpoint") - 1
    setHeatingSetpoint(nextLevel)
}

def quickSetHeat(degrees) {
	setHeatingSetpoint(degrees, 1000)
}

def setHeatingSetpoint(degrees, delay = 5000) {
	setHeatingSetpoint(degrees.toDouble(), delay)
}

def setHeatingSetpoint(Double degrees, Integer delay = 5000) {
	if (state.debug) log.debug "setHeatingSetpoint...START"
    def locationScale = getTemperatureScale()
    if (state.debug) log.debug "stateScale is $state.scale"
    if (state.debug) log.debug "locationScale is $locationScale"
	def p = (state.precision == null) ? 1 : state.precision
    if (state.debug) log.debug "setpoint requested is $degrees"
    if (state.debug) log.debug "setHeatingSetpoint...END"
    
    state.setHeatingSetpoint = degrees
    
    sendEvent(name:"heatingSetpoint", value:state.setHeatingSetpoint, isStateChange: true)

    secureSequence([
		zwave.thermostatSetpointV1.thermostatSetpointSet(setpointType: 1, scale: 0, precision: 1, scaledValue: state.setHeatingSetpoint),
        zwave.thermostatSetpointV1.thermostatSetpointGet(setpointType: 1),
        //zwave.sensorMultilevelV3.sensorMultilevelGet(), // current temperature
		
		//zwave.thermostatModeV2.thermostatModeGet(),
		//zwave.thermostatOperatingStateV1.thermostatOperatingStateGet(),
        //zwave.clockV1.clockGet(),
        //zwave.scheduleV1.scheduleStateReport()

	])
     
    
    //poll()
}




def offmode() {
	if (state.debug) log.debug "Switching to off mode..."
    sendEvent(name: "currentMode", value: "Off" as String)
	delayBetween([
		zwave.thermostatModeV2.thermostatModeSet(mode: 0).format(),
		zwave.thermostatModeV2.thermostatModeGet().format(),
        zwave.thermostatOperatingStateV1.thermostatOperatingStateGet().format()
	], 3000)
}

def on() {
	if (state.debug) log.debug "Setting thermostat fan mode to circulate..."
    fanCirculate()
}

def off() {
	if (state.debug) log.debug "Setting thermostat fan mode to auto..."
    fanAuto()
}

def heat() {
	if (state.debug) log.debug "Switching to heat mode..."
    sendEvent(name: "currentMode", value: "Heat" as String)
	delayBetween([
		zwave.thermostatModeV2.thermostatModeSet(mode: 1).format(),
		zwave.thermostatModeV2.thermostatModeGet().format(),
        zwave.thermostatOperatingStateV1.thermostatOperatingStateGet().format()
	], 3000)   
}



def auto() {
	if (state.debug) log.debug "Switching to auto mode..."
    sendEvent(name: "currentMode", value: "Auto" as String)
	delayBetween([
		zwave.thermostatModeV2.thermostatModeSet(mode: 3).format(),
		zwave.thermostatModeV2.thermostatModeGet().format(),
        zwave.thermostatOperatingStateV1.thermostatOperatingStateGet().format()
	], 3000)
}

def emergencyHeat() {
	if (state.debug) log.debug "Switching to emergency heat mode..."
    sendEvent(name: "currentMode", value: "E-Heat" as String)
	delayBetween([
		zwave.thermostatModeV2.thermostatModeSet(mode: 4).format(),
		zwave.thermostatModeV2.thermostatModeGet().format(),
        zwave.thermostatOperatingStateV1.thermostatOperatingStateGet().format()
	], 3000)
}



def poll() {
	if (state.debug) log.debug "Executing poll/refresh...."
    
    //setClock()
    
    secureSequence([
		//zwave.thermostatSetpointV1.thermostatSetpointSet(setpointType: 1, scale: 0, precision: 1, scaledValue: state.setHeatingSetpoint),
        zwave.sensorMultilevelV3.sensorMultilevelGet(), // current temperature
		zwave.thermostatSetpointV1.thermostatSetpointGet(setpointType: 1),
		//zwave.thermostatModeV2.thermostatModeGet(),
		//zwave.thermostatOperatingStateV1.thermostatOperatingStateGet(),
        //zwave.clockV1.clockGet(),
        //zwave.scheduleV1.scheduleStateReport()

	]) + ["delay 1000", zwave.wakeUpV1.wakeUpNoMoreInformation().format()]
    
   
	
}



def setClock() {	// once a day
	//def nowTime = new Date().time
	//def ageInMinutes = state.lastClockSet ? (nowTime - state.lastClockSet)/60000 : 1440
    //log.debug "Clock set age: ${ageInMinutes} minutes"
    //if (ageInMinutes >= 1440) {
		state.lastClockSet = nowTime
        def nowCal = Calendar.getInstance(location.timeZone) // get current location timezone
		log.debug "Setting clock to ${nowCal.getTime().format("EEE MMM dd yyyy HH:mm:ss z", location.timeZone)}"
        sendEvent(name: "SetClock", value: "setting clock to ${nowCal.getTime().format("EEE MMM dd yyyy HH:mm:ss z", location.timeZone)}", displayed: true, isStateChange: true)
		//secure(zwave.clockV1.clockSet(hour: nowCal.get(Calendar.HOUR_OF_DAY), minute: nowCal.get(Calendar.MINUTE), weekday: nowCal.get(Calendar.DAY_OF_WEEK)))
       // secure( zwave.clockV1.clockGet())
        
         secureSequence([
        //zwave.clockV1.clockGet()
        zwave.sensorMultilevelV3.sensorMultilevelGet()

	]) + ["delay 2000", zwave.wakeUpV1.wakeUpNoMoreInformation().format()]
        
    //} else "delay 87"
}




// PING is used by Device-Watch in attempt to reach the Device
def ping() {
	poll()
}

def configure() {
	if (state.debug) log.debug "Executing configure...."
	delayBetween([
		zwave.thermostatModeV2.thermostatModeSupportedGet().format(),
		zwave.thermostatFanModeV3.thermostatFanModeSupportedGet().format(),
		zwave.associationV1.associationSet(groupingIdentifier:1, nodeId:[zwaveHubNodeId]).format()
	], 2300)
    if (state.debug) log.debug "....done executing configure"
}

private getStandardDelay() {
	1000
}

private secure(physicalgraph.zwave.Command cmd) {
	zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd).format()
}

private secureSequence(commands, delay=3000) {
	delayBetween(commands.collect{ secure(it) }, delay)
}

