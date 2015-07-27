Field 1: MSG
Field 2: Transmission Type
Field 3: System-generated SessionID
Field 4: System-generated AircraftID
Field 5: HexIdent
Field 6: System-generated FlightID
Field 7: Date message generated
Field 8: Time message generated
Field 9: Date message logged
Field 10: Time message logged
Field 11: Callsign
Field 12: Altitude
Field 13: GroundSpeed
Field 14: Track
Field 15: Lat
Field 16: Long
Field 17: VerticalRate
Field 18: Squawk
Field 19: Alert
Field 20: Emergency
Field 21: SPI
Field 22: IsOnGround

However, it gets a bit more complicated, as you will see if you look in the log files, because not all messages set all fields. Basically, you have to look at field 2, the TransmissionType. This can have the following values:

1 = ID Message (1090ES DF17 or ELS DAP)
2 = Surface Position Message (1090ES DF17)
3 = Airborne Position Message (1090ES DF17)
4 = Airborne Velocity Message (1090ES DF17)
5 = Surveillance Altitude Message (DF4, DF20)
6 = Surveillance ID (Squawk) Message (DF5, DF21)
8 = All-Call Reply/TCAS Acquisition Squitter (DF11)

These messages have values in the following fields:

IDMessage: Callsign
Surface Position Message: Altitude, GroundSpeed, Track, Lat, Long
Airborne Position Message: Altitude, Lat, Long, Alert, Emergency, SPI
Airborne Velocity Message: GroundSpeed, Track, VerticalRate
Surveillance Altitude Message: Altitude, Alert, SPI
Surveillance ID (Squawk) Message: Altitude, Squawk, Alert, Emergency, SPI
All-Call Reply: None at the moment
