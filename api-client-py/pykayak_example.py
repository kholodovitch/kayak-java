import pykayak
import time
ks = pykayak.KayakSearch('mCHpMz2L_a_OrEkz3FSiDg')
ks.start_search("SLC","AMM","n","05/26/2014","05/28/2014",travelers=1);
kp = pykayak.KayakParser()
complete = False
success = False
search_result_data = []
while not complete:
    iter_results = ks.get_results(1)
    if ks.is_complete(iter_results):
        complete = True
        search_result_raw_data = ks.get_results(ks.count)
        print search_result_raw_data;
        if(search_result_raw_data is not None):
            try:
                search_result_data = kp.parse(search_result_raw_data)
            except Exception, e:
                break
        
    time.sleep(5)
    
print search_result_data
