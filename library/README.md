HomeSystem
==========
This is a non-UI service that developers can bind to recevie data from sensor platforms. There are overall four services: SensorReportService, VeraService, VerisService and RaritanService.

SensorReportService
==========
This service is used to get sensor information of all sensor devices at home. It will store sensor devices' info into a hashmap data structure indexed by their names.

VeraService
==========
VeraService is used to retrieve data from Vera Devices using HTTP request.

VerisService
==========
VerisService is used to receive data from Veris E30 using Modbus RTU protocol.

RaritanService
==========
RaritanService is used to receive data from Raritan Dominion using SNMP (Simple Network Management Protoco).
