#!/usr/bin/python
# -*- coding: utf-8 -*-
"""Compile data on the number of objects that over time for a histogram
        Retrieves the json data from the dccd-rest interface
        Example output:
        This file could then be read by a webapplication to show a summary chart using Morris.js

Requires:
        docopt
        requests

Usage:
        dccd_objects_over_time.py
        dccd_objects_over_time.py ( -h | --help)

Options:
        -h --help         Show this screen

"""

from docopt import docopt

import json
import urllib2, base64


def main(argv={}):
        user = "%%%TESTUSER%%%"
        pwd = "%%%TESTPWD%%%"
        outputPath = "/opt/dccd-home/data/dccd_objects_over_time.json"
        
        #http://localhost:8080/dccd-rest/rest/object/query?limit=0&lastYearFrom=-2000&firstYearTo=-1000
        url = 'http://localhost:8080/dccd-rest/rest/object/query?limit=0'
        firstyear = -6000
        lastyear = 2100
        step = 100

        # Set up output file
        f = open(outputPath, 'w')
        f.write('[\n')
        print "Collecting data about objects over time:"

        # Loop through categories sending webservice requests for each
        for x in range(firstyear, lastyear, step):

                # Compile webservice URL
                url2 = url + '&lastYearFrom=' + str(x) + '&firstYearTo=' + str(x+step)
                req = urllib2.Request(url2)

                # Set up headers to authenticate with our test user
                req.add_header('Accept', 'application/json')
                base64string = base64.encodestring('%s:%s' % (user, pwd)).replace('\n', '')
                req.add_header("Authorization", "Basic %s" % base64string)

                # Send request
                resp = urllib2.urlopen(req)
                data = json.loads(resp.read())

                # Write response to console and output file
                yearrange = str(x) + ' - ' + str(x+step)
                objcount =  int(data["projects"]["@total"])
                print '  Years ' + yearrange + ' = ' + str(objcount)
                strg = '   {label: "' + yearrange + '", value: ' + str(objcount) + '},\n'
                f.write(strg)

        # Finish output file and close
        f.write(']')
        f.close();

if __name__ == '__main__':
        main(docopt(__doc__))
                
