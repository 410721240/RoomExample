package com.example.roomexample.network

data class WeatherData(val records: Location)

data class Location(val location: List<Item>)

data class Item(val locationName: String, val weatherElement: List<Element>)

data class Element(val time: List<TimeSlot>)

data class TimeSlot(val startTime: String, val endTime: String, val parameter: Description)

data class Description(val parameterName: String)

//extension function to do data transformation
fun WeatherData.asTemperatureEntity(): TemperatureEntity {
    return TemperatureEntity(
        city = this.records.location[0].locationName,
        summary = this.records.location[0].weatherElement[0].time[0].parameter.parameterName,
        rainProb = this.records.location[0].weatherElement[1].time[0].parameter.parameterName + " %",
        minT = this.records.location[0].weatherElement[2].time[0].parameter.parameterName + " \u2103",
        feeling = this.records.location[0].weatherElement[3].time[0].parameter.parameterName,
        maxT = this.records.location[0].weatherElement[4].time[0].parameter.parameterName + " \u2103",
    )
}