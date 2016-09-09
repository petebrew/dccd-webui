#!/usr/bin/python
# -*- coding: utf-8 -*-
"""Compile data on the number of objects submitted by each organisation
        Retrieves the json data from the dccd-rest interface
        Example output:
        This file could then be read by a webapplication to show a summary chart using Morris.js

Requires:
        docopt
        requests

Usage:
        submissions_by_organisations.py
        submissions_by_organisations.py ( -h | --help)

Options:
        -h --help         Show this screen

"""

from docopt import docopt

import json
import urllib2, base64


def main(argv={}):
        user = "%%%TESTUSER%%%"
        pwd = "%%%TESTPWD%%%"
        outputPath = "/opt/dccd-home/data/submissions_by_organisations.json"
        url = 'http://localhost:8080/dccd-rest/rest/organisation/'
		orgs = []

        # Set up output file
        f = open(outputPath, 'w')
        f.write('[\n')

        # Compile webservice URL
        req = urllib2.Request(url)

        # Set up headers to authenticate with our test user
        req.add_header('Accept', 'application/json')

        # Send request
        resp = urllib2.urlopen(req)
        data = json.loads(resp.read())

        print "Compiling list of organisations"
        for org in data["organisations"]["organisation"]:
                orgs.append(org["id"])

        print "Searching for objects submitted by each organisation"
        for org in orgs:
                orgid = org.replace(' ', '+')
                url2 = 'http://localhost:8080/dccd-rest/rest/object/query?project.organisation.id=' + orgid + '&limit=0'
                 req = urllib2.Request(url2)

                # Set up headers to authenticate with our test user
                req.add_header('Accept', 'application/json')
                base64string = base64.encodestring('%s:%s' % (user, pwd)).replace('\n', '')
                req.add_header("Authorization", "Basic %s" % base64string)

                # Send request
                resp = urllib2.urlopen(req)
                data = json.loads(resp.read())

                # Write response to console and output file
                count = num(data["projects"]["@total"])
                print '  ' + orgid + ' = ' + str(count)
                string = '   {label: "' + orgid + '", value: ' + str(count) + '},\n'
                runningtotal += count
                f.write(string)

        # Finish output file and close
        f.write(']')
        f.close();

if __name__ == '__main__':
        main(docopt(__doc__))




if __name__ == '__main__':
        main(docopt(__doc__))
                
