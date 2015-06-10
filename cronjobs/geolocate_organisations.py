#!/usr/bin/python
# -*- coding: utf-8 -*-
"""geolocate organisations in the DCCD archive
	Retrieves the json data on organisations from the dccd-rest interface (also can read from file)
	and uses the city and country information to determine the geolocation (lat,lon).
	It uses the RESTfull OpenStreatMaps service at http://nominatim.openstreetmap.org .
	When it has the geolocation (WGS84 lat lon in decimal units) it writes it to a json file.
	Example output:
	{"markers": [{"lat": "42.4396039", "info": "TEST1", "lon": "-76.4968019"},
	{"lat": "47.237953", "info": "TEST2", "lon": "6.0243246"}]}.
	This file could then be read by a webapplication to show the markers on the map

Requires:
	docopt
	requests
	
Usage:
	geolocate_organisations.py
	geolocate_organisations.py ( -h | --help)
	
Options:
	-h --help         Show this screen
	
"""
from docopt import docopt

import requests
import os
import re
import json

# Curl example: 
# $ curl "http://nominatim.openstreetmap.org/search?format=json&limit=1&country=Netherlands&city=Amsterdam"
#
def getLocation(city, country):
	# no need to url encode city and country params; requests is doing that
	restURL = "http://nominatim.openstreetmap.org/search"
	format = "json"
	limit = "1"
	# ask for json
	headers = {'accept': 'application/json'}
	#params = {'format':format, 'limit':limit, 'country':country, 'city':city}
	# more flexible query gives better results
	params = {'format':format, 'limit':limit, 'q':city  + ', ' + country}
	r = requests.get(restURL, headers=headers, params=params)
	if r.status_code != requests.codes.ok : 
		print "Error; could not retrieve data!"
	#print "Status: " + str(r.status_code) + "; response: " + r.text
	# TODO handle errors
	results = r.json()
	# assume one and only one result
	result = results[0]
	location = {}
	location['lat'] = result['lat']
	location['lon'] = result['lon']
	return location
	
def getOrganisationsFromUrl(restURL):
	# could read it from a file previously retrieved
	headers = {'accept': 'application/json'}
	r = requests.get(restURL, headers=headers)
	if r.status_code != requests.codes.ok : 
		print "Error; could not retrieve data!"
	#print "Status: " + str(r.status_code) + "; response: " + r.text
	# TODO handle errors
	results = r.json()['organisations']
	return results

	# Note: each organisation will have an id, and optional city and country properties
	# but we need at least a city	

def getOrganisationsFromFile(filePath):
	# used for testing etc.
	json_data = open(filePath).read()
	results = json.loads(json_data)
	return results['organisations']

""" main """
def main(argv={}):
	outputPath = "/opt/dccd-home/data/geolocation_organisations.json"
	restURL = "http://localhost:8080/dccd-rest/rest/organisation"
	
	print "Geolocate organisations in the DCCD archive"
	print "Retrieving json data from: " + restURL
	print "Writing json data to:      " +  outputPath
	print ""
	
	organisationsContainer = getOrganisationsFromUrl(restURL)

	# alternatively, you could load the json from a file
	#filePath = "dccd-organisations.json" #"organisations.json"
	#organisationsContainer = getOrganisationsFromFile(filePath)
	
	organisations = []
	if type(organisationsContainer['organisation']) is list:
		organisations = organisationsContainer['organisation']
	else:
		# fix single result problem
		organisations.append(organisationsContainer['organisation'])
	
	numOrgs = 0
	numFound = 0
	numSkipped = 0
	markerCollection = {'markers':[]}
	# try to find a location for each organisation
	for org in organisations:
		numOrgs+=1
		id = org['id']
		print 'Got organisation: ' + id
		if 'city' not in org:
			print 'Skipping this one because it has no city'
			numSkipped+=1
			continue # skip this one
		city = org['city']
		country = ''
		if 'country' in org:
			country = org['country'] 
		try:	
			location = getLocation(city, country)
			# TODO check if we have useful location
			info = id #+ ',' + city + ',' + country # add for TESTING!
			print 'Found location: ' + info + '=' + '[' + location['lat'] + ',' + location['lon'] + ']'	
			# just have markers with lat, lon, info
			marker = {'lat':location['lat'],'lon':location['lon'],'info':info}
			markerCollection['markers'].append(marker)
			numFound+=1
		except:
			print "Could not find location"
		
	# Save all into a json file
	f = open(outputPath, 'w')
	f.write(json.JSONEncoder().encode(markerCollection))
	f.close()
	
	# Instead of the markers, shall I make some nice geojson for display?
	#geoPoint = {'type': 'Point', 'coordinates': []}
	#geoPoint['coordinates'] = [location['lat'], location['lon']];
	#geoFeature = {'type': 'Feature', 'geometry': {}, 'properties': {}}
	#geoFeature['geometry'] = geoPoint
	#geoFeature['properties'] = {'info': info}
	#geoFeatureCollection = {'type': 'FeatureCollection', 'features': []}
	#geoFeatureCollection['features'].append(geoFeature)
	#f = open('organisations.json', 'w')
	#f.write(json.JSONEncoder().encode(geoFeatureCollection))
	#f.close()
	
	print ""
	print "Geolocating results"
	print "==================="
	print "#organisations: " + str(numOrgs)
	print "#found:         " + str(numFound)
	print "#skipped:       " + str(numSkipped)
	print "#failed:        " + str(numOrgs - numFound - numSkipped)

if __name__ == '__main__':
	main(docopt(__doc__))
