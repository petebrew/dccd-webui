#!/usr/bin/python
# -*- coding: utf-8 -*-
"""Compile data on the number of objects that use each project category
        Retrieves the json data from the dccd-rest interface
        Example output:
        This file could then be read by a webapplication to show a summary chart using Morris.js

Requires:
        docopt
        requests

Usage:
        dccd_stats_categories.py
        dccd_stats_categories.py ( -h | --help)

Options:
        -h --help         Show this screen

"""

from docopt import docopt

import json
import urllib2, base64


def main(argv={}):
        user = "%%%TESTUSER%%%"
        pwd = "%%%TESTPWD%%%"
        categories = ['archaeology','built heritage','furniture','mobilia','musical instrument','painting','palaeo-vegetation','ship archaeology','standing trees','woodcarving','other']
        outputPath = "/opt/dccd-home/data/dccd_project_categories.json"
        url = 'http://localhost:8080/dccd-rest/rest/object/query?category='

		# Set up output file
        f = open(outputPath, 'w')
        f.write('[\n')
        print "Collecting statistics on project category useage:"
        
        # Loop through categories sending webservice requests for each
        for category in categories:
        
        		# Compile webservice URL request replacing spaces with +
                categ = category.replace(' ', '+')
                url2 = url + categ
                req = urllib2.Request(url2)
                
                # Set up headers to authenticate with our test user
                req.add_header('Accept', 'application/json')
                base64string = base64.encodestring('%s:%s' % (user, pwd)).replace('\n', '')
                req.add_header("Authorization", "Basic %s" % base64string)

				# Send request
                resp = urllib2.urlopen(req)
                data = json.loads(resp.read())

				# Write response to console and output file
                print '  ' + category + ' = ' +data["projects"]["@total"]
                str = '   {label: "' + category + '", value: ' + data["projects"]["@total"] + '},\n'
                f.write(str)
                
        # Finish output file and close
        f.write(']')
        f.close();

if __name__ == '__main__':
        main(docopt(__doc__))
                
