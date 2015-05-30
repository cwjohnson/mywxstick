/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var xhr = null;
var xhr_charts = null;
var xhr_pressurechart = null;
var xhr_precipchart = null;

/*
 * global so we can update them in the temperature gauge - there is probably
 * a more correct way.
 */
var temperature_low = 0;
var temperature_high = 0;
var temperature_current = 0;

function updateElementJQ(id, value, unit) {
	var str = value + unit;
	$(id).hide().text(str).fadeIn(750);
}

function updateCurrentConditions() {
	xhr.open('GET', 'observice/currentconditions/test', true);
	xhr.send(null);	
}

function updateCharts()
{
	xhr_charts.open('GET', 'observice/currentcondition/test/timeseries?vars=temperature_air,humidity', true);
	xhr_charts.send(null);	
	xhr_pressurechart.open('GET', 'observice/currentcondition/test/timeseries?vars=sea_level_pressure', true);
	xhr_pressurechart.send(null);
	xhr_precipchart.open('GET', 'observice/currentcondition/test/timeseries/precipaccumlast24hr', true);
	xhr_precipchart.send(null);
}

// JQuery shortcut for page ready event
$(function () {
	

	// AJAX load json from the server
	// server returns Observation object
	xhr = new XMLHttpRequest();
	xhr.onload = function() {
		if (xhr.status === 200) {
			cc = JSON.parse(xhr.responseText);

			temperature_low = (cc.lowTemperatureAir * 9.0 / 5.0 + 32.0);
			temperature_high = (cc.highTemperatureAir * 9.0 / 5.0 + 32.0);
			temperature_current = (cc.currentObservation.temperatureAir * 9.0 / 5.0 + 32.0);
			var date = new Date(parseInt(cc.currentObservation.obTime));
			updateElementJQ ('th#obtime', date.toString(), "");
			updateElementJQ ('td#temperature_no_decimal', temperature_current.toFixed(0), "°F");
			updateElementJQ ('td#temperature', temperature_current.toFixed(1), "°F");
			updateElementJQ ('td#temperaturemaxmin', temperature_high.toFixed(1) + "/" +
													 temperature_low.toFixed(1), "°F")
			updateElementJQ ('td#humidity', (cc.currentObservation.humidity).toFixed(1), "%");
			updateElementJQ ('td#humidity_no_decimal', (cc.currentObservation.humidity).toFixed(0), "%");
			updateElementJQ ('td#humiditymax', cc.highHumidity.toFixed(1), "%")
			updateElementJQ ('td#humiditymin', cc.lowHumidity.toFixed(1), "%")
			updateElementJQ ('td#pressure', (cc.currentObservation.seaLevelPressure).toFixed(1), "mb");
			updateElementJQ ('td#pressureTendency', cc.pressureTendency, "");
			updateElementJQ ('td#windspeed', (cc.currentObservation.windSpeed).toFixed(0), "mph");
			updateElementJQ ('td#watertemperature', (cc.currentObservation.temperatureWater * 9.0 / 5.0 + 32.0).toFixed(1), "°F");
			updateElementJQ ('td#watertemperaturemax', (cc.highTemperatureWater * 9.0 / 5.0 + 32.0).toFixed(1), "°F");
			updateElementJQ ('td#watertemperaturemin', (cc.lowTemperatureWater * 9.0 / 5.0 + 32.0).toFixed(1), "°F");
		}
	};
	
	xhr_charts = new XMLHttpRequest();
	xhr_charts.onload = function() {
		if (xhr_charts.status === 200) {
			var jsondata = JSON.parse(xhr_charts.responseText);
			var len = jsondata.dates.length;
			var plotdata = [];
			var humiditydata = [];
			for (var i = 0 ; i < len ; i++) {
				plotdata[i] = [jsondata.dates[i], (jsondata.series.temperature_air[i] * 9.0 / 5.0 + 32.0)];
				humiditydata[i] = [jsondata.dates[i], jsondata.series.humidity[i]];
			}
			$('#temperaturechart').highcharts({
				chart: {
					//type: 'line'
					zoomType: 'x'
				},
				title: {
					enabled: false,
					text: 'Temperature/Humidity'
				},
				xAxis: {
					tickInterval: 3600000,
					type: 'datetime',
					title: {
						enabled: false,
						text: 'Time'
					}
				},
				yAxis: [{
	                title: {
						text: 'Temperature °F',
					},
	                labels: {
	                    format: '{value}',
	                    style: {
	                        color: Highcharts.getOptions().colors[1]
	                    }
	                },
					lineColor: '#FF0000',
					tickInterval: 10,
				},{
					title: {
						text: 'Humidity %',
					},
					labels: {
	                    format: '{value}',
	                    style: {
	                        color: Highcharts.getOptions().colors[1]
	                    }
					},
					opposite: true,
					lineColor: '#0000FF',
					alignTicks: false,
					min: 0,
					max: 100,
				}],
	            legend: {
	                enabled: false
	            },
	            plotOptions: {
	                area: {
	                    fillColor: {
	                        linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1},
	                        stops: [
	                            [0, Highcharts.getOptions().colors[0]],
	                            [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
	                        ]
	                    },
	                    marker: {
	                        radius: 2
	                    },
	                    lineWidth: 1,
	                    states: {
	                        hover: {
	                            lineWidth: 1
	                        }
	                    },
	                    threshold: null
	                }
	            },
	            series: [{
					name: "Temperature °F",
					data: plotdata,
	                tooltip: {
	                    valueSuffix: '°F'
	                }
	            }, {
					name: "Humidity %",
					data: humiditydata,
	                tooltip: {
	                    valueSuffix: '%'
	                }
                }],
			});
		}
	}

	xhr_pressurechart = new XMLHttpRequest();
	xhr_pressurechart.onload = function() {
		if (xhr_pressurechart.status === 200) {
			var jsondata = JSON.parse(xhr_pressurechart.responseText);
			var len = jsondata.dates.length;
			var plotdata = [];
			for (var i = 0 ; i < len ; i++) {
				plotdata[i] = [jsondata.dates[i], jsondata.series.sea_level_pressure[i]];
			}
			$('#pressurechart').highcharts({
				chart: {
					//type: 'line'
					zoomType: 'x'
				},
				title: {
					enabled: false,
					text: 'Sea Level Pressure'
				},
				xAxis: {
					tickInterval: 3600000,
					type: 'datetime',
					title: {
						enabled: false,
						text: 'Time'
					}
				},
				yAxis: [{
	                labels: {
	                    format: '{value}mb',
	                    style: {
	                        color: Highcharts.getOptions().colors[1]
	                    }
	                },
	                title: {
	                	enabled: false,
						text: 'SLP mb',
					}
				}],
	            legend: {
	                enabled: false
	            },
	            plotOptions: {
	                area: {
	                    fillColor: {
	                        linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1},
	                        stops: [
	                            [0, Highcharts.getOptions().colors[0]],
	                            [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
	                        ]
	                    },
	                    marker: {
	                        radius: 2
	                    },
	                    lineWidth: 1,
	                    states: {
	                        hover: {
	                            lineWidth: 1
	                        }
	                    },
	                    threshold: null
	                }
	            },
	            series: [{
					name: "Pressure (mb)",
					data: plotdata,
	                tooltip: {
	                    valueSuffix: 'mb'
	                }
	            }],
			});
		}
	}

	xhr_precipchart = new XMLHttpRequest();
	xhr_precipchart.onload = function() {
		if (xhr_precipchart.status === 200) {
			var jsondata = JSON.parse(xhr_precipchart.responseText);
			var len = jsondata.dates.length;
			var precipdata = [];
			var runningsumdata = [];
			for (var i = 0 ; i < len ; i++) {
				precipdata[i] = [jsondata.dates[i], jsondata.series.precip[i]];
				runningsumdata[i] = [jsondata.dates[i], jsondata.series.runningsum[i]];
			}
			$('#precipchart').highcharts({
				chart: {
					zoomType: 'x'
				},
				title: {
					enabled: false,
					text: '24hr Precipitation'
				},
				xAxis: {
					tickInterval: 3600000,
					type: 'datetime',
					title: {
						enabled: false,
						text: 'Time'
					},
				    dateTimeLabelFormats : {
				        hour: '%I %p',
				        minute: '%I:%M %p',
				        day: '%a'
				    }
				},
				yAxis: [{
	                labels: {
	                    format: '{value}',
	                    style: {
	                        color: Highcharts.getOptions().colors[0]
	                    }
	                },
	                title: {
						text: 'Hourly',
					},
					lineColor: '#FF0000',
					min: 0,
					tickInterval: 0.1,
				},{
					labels: {
	                    format: '{value}',
	                    style: {
	                        color: Highcharts.getOptions().colors[1]
	                    }
					},
					title: {
						text: 'Total',
					},
					opposite: true,
					lineColor: '#0000FF',
					min: 0,
				}],
	            legend: {
	                enabled: false
	            },
	            plotOptions: {
	                area: {
	                    fillColor: {
	                        linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1},
	                        stops: [
	                            [0, Highcharts.getOptions().colors[0]],
	                            [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
	                        ]
	                    },
	                    marker: {
	                        radius: 2
	                    },
	                    lineWidth: 1,
	                    states: {
	                        hover: {
	                            lineWidth: 1
	                        }
	                    },
	                    threshold: null
	                }
	            },
	            series: [{
					name: "Hourly",
					type: 'column',
					data: precipdata,
	                tooltip: {
	                    valueSuffix: 'In'
	                }
	            }, {
					name: "Total",
					type: 'spline',
					data: runningsumdata,
	                tooltip: {
	                    valueSuffix: 'In'
	                }
                }],
			});
		}
	}	
	
	// Create the chart
    $('#temperaturemaxminchart').highcharts({

        chart: {
            type: 'gauge',
			backgroundColor: 'rgba(255,255,255,0.0)',
            plotBackgroundColor: 'rgba(255, 255, 255, 0.0)',
            plotBackgroundImage: null,
            plotBorderWidth: 0,
            plotShadow: false,
            height: 200
        },

        credits: {
            enabled: false
        },

        title: {
            text: 'Temperature'
        },

        pane: {
			startAngle: -150,
			endAngle: 150,
            background: [{
                // default background
            }, {
                // reflex for supported browsers
                backgroundColor: Highcharts.svg ? {
                    radialGradient: {
                        cx: 0.5,
                        cy: -0.4,
                        r: 1.9
                    },
                    stops: [
                        [0.5, 'rgba(255, 255, 255, 0.2)'],
                        [0.5, 'rgba(200, 200, 200, 0.2)']
                    ]
                } : null
            }]
        },

        yAxis: {
            labels: {
                distance: -20
            },
            min: 50,
            max: 80,
            lineWidth: 0,
            showFirstLabel: false,

            minorTickInterval: 'auto',
            minorTickWidth: 1,
            minorTickLength: 5,
            minorTickPosition: 'inside',
            minorGridLineWidth: 0,
            minorTickColor: '#666',

            tickInterval: 5,
            tickWidth: 2,
            tickPosition: 'inside',
            tickLength: 10,
            tickColor: '#666',
            title: {
                //text: 'Powered by<br/>Highcharts',
                style: {
                    color: '#BBB',
                    fontWeight: 'normal',
                    fontSize: '8px',
                    lineHeight: '10px'
                },
                y: 10
            }
        },

        tooltip: {
            formatter: function () {
                return this.series.chart.tooltipText;
            }
        },

        series: [{
            data: [{
                id: 'low',
                y: 55,
                dial: {
                    radius: '60%',
					backgroundColor: '#0000FF',
                    baseWidth: 4,
                    baseLength: '95%',
                    rearLength: 0
                }
            }, {
                id: 'high',
                y: 72,
                dial: {
				backgroundColor: '#FF0000',
				baseWidth: 4,
                    baseLength: '95%',
					rearLength: 0
                }
            }, {
                id: 'current',
                y: 65,
                dial: {
                    radius: '100%',
                    baseWidth: 2,
                    rearLength: 0
                }
            }],
            animation: false,
            dataLabels: {
                enabled: false
            }
        }]
    },

	// Move
	function (chart) {
		setInterval(function () {

			var low = chart.get('low'),
				high = chart.get('high'),
				current = chart.get('current');

			var l = temperature_low;
			var c = temperature_current;
			var h = temperature_high;
			
			// Cache the tooltip text
			chart.tooltipText = l.toFixed(1) + "/" +
								c.toFixed(1) + "/" +
								h.toFixed(1);

			low.update(Math.round(l), true, null);
			high.update(Math.round(h), true, null);
			current.update(Math.round(c), true, null);

		}, 1000);

    });
	
	// update current conditons on clicking refresh
	$('#currentconditions').on('click', '#refresh', function(e) {
		updateCurrentConditions();
		updateCharts();
	});
	
	// update current condition on ready
	updateCurrentConditions();
	updateCharts();
		
	// update curent condtions every interval
	setInterval(updateCurrentConditions, 15000);
	setInterval(updateCharts, 15000);
});
