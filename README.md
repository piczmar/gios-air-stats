## Serverless GIOS Stats
Serverless scheduled task gathering statistic of earth quality in Lodz, Poland
using [API](http://powietrze.gios.gov.pl/pjp/content/api) of CHIEF INSPECTORATE FOR ENVIRONMENTAL PROTECTION.

API exposes data from sensors with 1h precision.

AWS Lambda scheduled function runs every 30min and gathers last available data.

Values of available sensors (e.g.: NO2, PM10, CO, C6H6) are stored in DynamoDB.