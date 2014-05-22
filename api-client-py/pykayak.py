#!/usr/bin/env python
import cookielib, urllib, urllib2
from xml.dom.minidom import parse,parseString
import xml.etree.cElementTree as ET
from datetime import datetime
import os
import time
import random
import math
#import airports

class KayakSearchCookiePolicy(cookielib.DefaultCookiePolicy):
    def set_ok(self, cookie, request):
        if (cookie.name == "cluster"):
            return True
        if not cookielib.DefaultCookiePolicy.set_ok(self, cookie, request):
            return False
        return True
    
    def return_ok(self, cookie, request):
        if (cookie.name == "cluster"):
            return True
        if not cookielib.DefaultCookiePolicy.return_ok(self, cookie, request):
            return False
        return True


class KayakSearch:
    COOKIE_FILE = 'c:\\windows\\temp\\' + str(random.randint(1, 1000000)) + '.txt'
    TOKEN = ''
    
    def __init__(self, token):
        kscp = KayakSearchCookiePolicy()
        self.cj = cookielib.LWPCookieJar(policy=kscp)
        if os.path.isfile(self.COOKIE_FILE):
            self.cj.load(self.COOKIE_FILE)
        self.cj.clear()
        #self.cj.extract_cookies()
        self.regex ={}
        self.headers = {
            'User-agent' : 'Mozilla/4.0 (compatible; MSIE 8.0; Windows NT)',
            'Cache-Control' : 'no-cache',
            'Pragma' : 'no-cache',
        }

        self.TOKEN = '7CPfBeTbBSdg$oPkEB_q1Q'

        
        self._opener = urllib2.build_opener(
            urllib2.HTTPCookieProcessor(self.cj)
        )
        urllib2.install_opener(self._opener)
        
        self.start_time = time.time()

        self.get_session();
        
    def get_session(self):
        url = 'http://api.kayak.com/k/ident/apisession?token='+self.TOKEN
        response_xml = self._get(url).read()
        dom = parseString(response_xml)
        
        self._sid = dom.getElementsByTagName('sid')[0].childNodes[0].nodeValue                
    
    def start_search(self, orig, dest, oneway, depart_date, return_date, \
               depart_time='a', return_time='a', travelers = 1, cabin='e'):
        
        url = 'http://api.kayak.com/s/apisearch'
        values = urllib.urlencode({
                    'basicmode' : 'true',
                    'oneway' : oneway,
                    'origin' : orig,
                    'destination' : dest,
                    'depart_date' : depart_date,
                    'return_date' : return_date,
                    'depart_time' : depart_time,
                    'return_time' : return_time,
                    'travelers' : travelers,
                    'cabin' : cabin,
                    'action' : 'doFlights',
                    'apimode' : '1',
                    '_sid_' : self._sid,
                    'version' : '1'               
                });
        retval = self._get(url + '?' + values)

        data = retval.read()
        dom = parseString(data)
        self._searchid = dom.getElementsByTagName('searchid')[0].childNodes[0].nodeValue 

            
    
    def get_results(self, c=10):
        
        url = 'http://api.kayak.com/s/basic/flight'
        values = urllib.urlencode({
                    'searchid' : self._searchid,
                    'apimode' : '1',
                    'c' : c,
                    'm' : 'normal',
                    'd' : 'up',
                    's' : 'price',
                    '_sid_' : self._sid,
                    'version' : '1'
                })
        
        #print values
        
        retval = self._get(url + '?' + values)
        data = None
        try:
            data = retval.read()
        except Exception, e:
            pass
        
        return data
        
    def _get(self, url):
        """
        Sends a GET request to the given url
        """
        request = urllib2.Request(url, headers=self.headers)
        retval = urllib2.urlopen(request)
        self.cj.save(self.COOKIE_FILE,ignore_discard=True)

        return retval
    
    def is_complete(self, results=None):
        if results is None:
            results = self.get_results()
        #print results
        dom = parseString(results)
        morepending = dom.getElementsByTagName('morepending')[0]
        count = int(dom.getElementsByTagName('count')[0].childNodes[0].nodeValue)
        complete = False
        if len(morepending.childNodes)==0 and count > 0:
            complete = True
            self.count = count
        elif time.time() - self.start_time > 60: # We've run out of time
            complete = True
            self.count = count
        
        return complete

class KayakParser:

    def parse(self,xml):
        trips = []
        et_doc = ET.fromstring(xml)
        trips_xml = et_doc.findall('*/trip')
        for trip_xml in trips_xml:
            trip_entry = self._parse_trip(trip_xml)
            if trip_entry['price'] == "-1": #Southwest / JetBlue
                continue
            trips.append(trip_entry)
        
        return trips

    def _parse_trip(self,xml):
        price = xml.find('price').text
        itinerary_id = xml.attrib["id"]
        booking_url = "http://api.kayak.com" + xml.find('price').attrib["url"]
        trip_legs_results = []
        trip_legs = xml.findall('*/leg')
        for trip_leg_xml in trip_legs:
            trip_legs_results.append(self._parse_leg(trip_leg_xml))

        depart_date = datetime.strptime(trip_legs_results[0]["depart_time"], "%Y-%m-%d %H:%M").strftime("%Y-%m-%d")
        return_date = datetime.strptime(trip_legs_results[-1]["depart_time"], "%Y-%m-%d %H:%M").strftime("%Y-%m-%d")
        depart_time = trip_legs_results[0]["depart_time"]
        arrive_time = trip_legs_results[0]["arrive_time"]
        return_depart_time =  trip_legs_results[-1]["depart_time"]
        return_arrive_time = trip_legs_results[-1]["arrive_time"]
        depart_airport = trip_legs_results[0]['orig']
        return_airport = trip_legs_results[-1]['orig']
        
        trip_length = datetime.strptime(trip_legs_results[-1]["depart_time"], "%Y-%m-%d %H:%M") - datetime.strptime(trip_legs_results[0]["depart_time"], "%Y-%m-%d %H:%M")

        min_layover_count = 999
        max_layover_count = 0
        for leg in trip_legs_results:
            if leg['stops'] < min_layover_count:
                min_layover_count = leg['stops']
            if leg['stops'] > max_layover_count:
                max_layover_count = leg['stops']




        trip_data = {   "price" : price,
                        "itinerary_id" : itinerary_id,
                        "booking_url" : booking_url,
                        "legs" : trip_legs_results,
                        "orig" : depart_airport,
                        "dest" : return_airport,
                        "depart_date" : depart_date,
                        "return_date" : return_date,
                        "depart_time" : depart_time,
                        "arrive_time" : arrive_time,
                        "return_depart_time" : return_depart_time,
                        "return_arrive_time" : return_arrive_time,
                        "retrieval_time" : datetime.now().strftime("%Y-%m-%d %H:%M"),
                        "trip_length" : trip_length.days
                    }
        
    
        
        return trip_data
    
    def _parse_leg(self,xml):
        
        segments = []
        
        leg_segments_xml = xml.findall('segment')
        for leg_segment_xml in leg_segments_xml:
            segments.append(self._parse_segment(leg_segment_xml))


        # Duration Minutes calculation

        min_layover_duration = 999999
        max_layover_duration = 0

        leg_duration = 0 # In minutes
        layover_land_time = None

        layovers = []

        for segment in segments:
            if leg_duration == 0: #First leg
                leg_duration += int(segment['duration_minutes'])
                layover_land_time = datetime.strptime(segment['arrive_time'],'%Y-%m-%d %H:%M')
            else: 
                layover_depart_time = datetime.strptime(segment['depart_time'],'%Y-%m-%d %H:%M')
                td = layover_depart_time - layover_land_time
                layover_duration = int((td.days * 24 * 60) + (td.seconds / 60))

                if layover_duration < min_layover_duration:
                    min_layover_duration = layover_duration
                
                if layover_duration > max_layover_duration:
                    max_layover_duration = layover_duration

                leg_duration += layover_duration
                leg_duration += int(segment['duration_minutes'])
                layover_land_time = datetime.strptime(segment['arrive_time'],'%Y-%m-%d %H:%M')
                layover = {
                    "airport" : segment['orig'],
                    "duration" : layover_duration,
                }
                layovers.append(layover)

            
        leg_data = {
            "airline" : xml.find('airline').text,
            "orig" : xml.find('orig').text,
            "dest" : xml.find('dest').text,
            "depart_time" : time.strftime('%Y-%m-%d %H:%M',time.strptime(xml.findall('*/dt')[0].text,'%Y/%m/%d %H:%M')),
            "arrive_time" : time.strftime('%Y-%m-%d %H:%M',time.strptime(xml.findall('*/at')[-1].text,'%Y/%m/%d %H:%M')),
            "stops" : xml.find('stops').text,
            "duration_minutes" : leg_duration,
            "segments" : segments,
            "layovers" : layovers,
            "min_layover_duration" : min_layover_duration,
            "max_layover_duration" : max_layover_duration
        }
        
        return leg_data
    
    def _parse_segment(self,xml):
        try:
            equip = xml.find('equip').text
        except Exception, e:
            equip = ''
        
        

        segment = {
        
          "airline" : xml.find('airline').text,
          "flight_number" : xml.find('flight').text,
          "duration_minutes" : xml.find('duration_minutes').text,
          "orig" : xml.find('o').text,
          "dest" : xml.find('d').text,
          "depart_time" : time.strftime('%Y-%m-%d %H:%M',time.strptime(xml.find('dt').text,'%Y/%m/%d %H:%M')),
          "arrive_time" : time.strftime('%Y-%m-%d %H:%M',time.strptime(xml.find('at').text,'%Y/%m/%d %H:%M')),
        }

        #start_lat = airports.airports[segment['orig']]['lat']
        #start_lon = airports.airports[segment['orig']]['lon']
        
        #end_lat = airports.airports[segment['dest']]['lat']
        #end_lon = airports.airports[segment['dest']]['lon']

        #segment["miles"] = self.distance_lat_lon((start_lat,start_lon),(end_lat,end_lon))     
        segment["miles"] = 0
        return segment
    
    def distance_lat_lon(self, start,  end):
      """
        Calculate distance (in miles) between two points given as (long, latt) pairs.
      """

      start_lat = float(start[0])
      start_lon = float(start[1])
      end_lat = float(end[0])
      end_lon = float(end[1])

      startLatRadCos = math.cos(math.radians(start_lat))
      startLatRadSin = math.sin(math.radians(start_lat))
      startLonRadCos = math.cos(math.radians(start_lon))
      startLonRadSin = math.sin(math.radians(start_lon))

      endLatRadCos = math.cos(math.radians(end_lat))
      endLatRadSin = math.sin(math.radians(end_lat))
      endLonRadCos = math.cos(math.radians(end_lon))
      endLonRadSin = math.sin(math.radians(end_lon))

      return math.acos((startLatRadCos * startLonRadCos * endLatRadCos * endLonRadCos) + \
            (startLatRadCos * startLonRadSin * endLatRadCos * endLonRadSin ) + \
            (startLatRadSin * endLatRadSin)) * 3963.1



