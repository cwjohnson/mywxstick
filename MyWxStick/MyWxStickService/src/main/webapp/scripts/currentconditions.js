/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var xhr = null;
var xhr_charts = null;

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
	xhr_charts.open('GET', 'observice/currentcondition/timeseries/test/temperature_air', true);
	xhr_charts.send(null);	
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
			updateElementJQ ('th#obtime', date.toLocaleString(), "");
			updateElementJQ ('td#temperature', temperature_current.toFixed(1), "F");
			updateElementJQ ('td#temperaturemaxmin', temperature_high.toFixed(1) + "/" +
													 temperature_low.toFixed(1), "F")
			updateElementJQ ('td#humidity', (cc.currentObservation.humidity).toFixed(1), "%");
			updateElementJQ ('td#humiditymax', cc.highHumidity.toFixed(1), "%")
			updateElementJQ ('td#humiditymin', cc.lowHumidity.toFixed(1), "%")
			updateElementJQ ('td#pressure', (cc.currentObservation.seaLevelPressure).toFixed(1), "mb");
			updateElementJQ ('td#windspeed', (cc.currentObservation.windSpeed).toFixed(0), "mph");
			updateElementJQ ('td#watertemperature', (cc.currentObservation.temperatureWater * 9.0 / 5.0 + 32.0).toFixed(1), "F");
			updateElementJQ ('td#watertemperaturemax', (cc.highTemperatureWater * 9.0 / 5.0 + 32.0).toFixed(1), "F");
			updateElementJQ ('td#watertemperaturemin', (cc.lowTemperatureWater * 9.0 / 5.0 + 32.0).toFixed(1), "F");
		}
	};
	
	xhr_charts = new XMLHttpRequest();
	xhr_charts.onload = function() {
		if (xhr_charts.status === 200) {
			var jsondata = JSON.parse(xhr_charts.responseText);
			var len = jsondata.dates.length;
			var plotdata = [];
			for (var i = 0 ; i < len ; i++) {
				plotdata[i] = [jsondata.dates[i], (jsondata.series.temperature_air[i] * 9.0 / 5.0 + 32.0)];
			}
			$('#temperaturechart').highcharts({
				chart: {
					type: 'line'
				},
				title: {
					text: 'Temperature'
				},
				xAxis: {
					tickInterval: 3600000,
					type: 'datetime',
					title: {
						text: 'Time'
					}
				},
				yAxis: {
					title: {
						text: 'Temperature (F)'
					}
				},
				series: [{
						name: "Temperature (F)",
						data: plotdata
				}]
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
