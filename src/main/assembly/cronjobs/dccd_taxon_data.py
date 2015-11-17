#!/usr/bin/python
# -*- coding: utf-8 -*-
"""Compile data on the number of objects that use each project taxon
        Retrieves the json data from the dccd-rest interface
        Example output:
        This file could then be read by a webapplication to show a summary chart using Morris.js

Requires:
        docopt
        requests

Usage:
        dccd_taxon_data.py
        dccd_taxon_data.py ( -h | --help)

Options:
        -h --help         Show this screen

"""

from docopt import docopt

import json
import urllib2, base64


def main(argv={}):
        user = "%%%TESTUSER%%%"
        pwd = "%%%TESTPWD%%%"
        taxa = ['Quercus', 'Pinus', 'Abies']
        outputPath = "/opt/dccd-home/data/dccd_taxon_data.json"
        totalurl = 'http://localhost:8080/dccd-rest/rest/object/query?limit=0'

        req = urllib2.Request(totalurl)

        # Set up headers to authenticate with our test user
        req.add_header('Accept', 'application/json')
        base64string = base64.encodestring('%s:%s' % (user, pwd)).replace('\n', '')
        req.add_header("Authorization", "Basic %s" % base64string)

        # Send request
        resp = urllib2.urlopen(req)
        data = json.loads(resp.read())

        totalobjects = num(data["projects"]["@total"])

        runningtotal = 0

        url = 'http://localhost:8080/dccd-rest/rest/object/query?element.taxon='


        # Set up output file
        f = open(outputPath, 'w')
        f.write('[\n')
        print "Collecting statistics on project taxon useage:"

        # Loop through taxa sending webservice requests for each
        for taxon in taxa:

                # Compile webservice URL request replacing spaces with +
                categ = taxon.replace(' ', '+')
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
                count = num(data["projects"]["@total"])
                print '  ' + taxon + ' = ' + str(count)
                string = '   {label: "' + taxon + '", value: ' + str(count) + '},\n'
                runningtotal += count
                f.write(string)


		# Calculate others
        remainder = totalobjects - runningtotal
        print '  Other = ' + str(remainder)
        string = '   {label: "other", value: ' + str(remainder) + '},\n'
        f.write(string)

        print '  -------------------------'
        print '  TOTAL  = ' + str(totalobjects)

        # Finish output file and close
        f.write(']')
        f.close();



def num(s):
    try:
        return int(s)
    except ValueError:
        return float(s)



if __name__ == '__main__':
        main(docopt(__doc__))

