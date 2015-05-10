/**
 * was starter
 */

var container = require('vertx/container');

var server_config = {
	"server": {
		"host": "0.0.0.0",
		"port": 8080,
		"useSSL": false,
		"channel": "ch_server"
	}

	"http_client" : {
		"channel": "ch_http_client"
	},
	"ftp_client" : {
		"channel": "ch_ftp_client"
	}
};

container.deployModule("org.lima.vertx~mod-was~0.0.1", 1, server_config);
