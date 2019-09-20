# SMS API

Send SMSes by sending a http request to android phone. 

This app starts a http server and listens for requests to send SMS. Both methods (POST and GET) are supported. Required params:

 - `recipient`: a mobile phone number with out plus sign, example: `48600700800`
 - `message`: SMS body to be sent
 
 ## Example request
 
 `curl -X POST -d 'recipient=48600700800&message=Hello world!' 192.168.1.100:8055`



