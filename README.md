# SMS API

Send SMSes by sending http request. 

This android app starts a http server and listens for requests to send SMS. Both methods (POST and GET) are supported. Required params:

 - `recipient`: a mobile phone number with out plus sign, example: `48600700800`
 - `message`: SMS body to be sent


