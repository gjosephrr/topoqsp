


SELECT * from ny_test n1, ny_test n2  
where ST_Intersects(n1.geom, n2.geom) 
AND n1.top_category = 'Offices of Physicians' 
AND n2.top_category = 'Automotive Repair and Maintenance'

SELECT * from ny_test n1, ny_test n2, ny_test n3
where ST_Intersects(n3.geom, n1.geom) 
AND ST_Equals(n1.geom ::geometry, n2.geom::geometry) 
AND n1.top_category = 'Offices of Physicians' 
AND n2.top_category = 'Automotive Repair and Maintenance' 
AND n3.top_category = 'Personal Care Services'

SELECT * from ny_test n1, ny_test n2, ny_test n3, ny_test n4 
where ST_Intersects(n3.geom,n1.geom) 
AND ST_Equals(n1.geom ::geometry, n2.geom::geometry)  
AND ST_Intersects(n3.geom, n4.geom)  
AND n1.top_category = 'Restaurants and Other Eating Places' 
AND n2.top_category = 'Automotive Repair and Maintenance' 
AND n3.top_category = 'Personal Care Services' 
AND n4.top_category = 'Religious Organizations'

SELECT * from ny_test n1, ny_test n2, ny_test n3, ny_test n4 
where ST_Intersects(n3.geom,n1.geom) 
AND ST_Equals(n1.geom ::geometry, n2.geom::geometry)  
AND ST_Intersects(n3.geom, n4.geom) 
AND ST_Intersects(n3.geom, n4.geom)  	 
AND n1.top_category = 'Restaurants and Other Eating Places' 
AND n2.top_category = 'Automotive Repair and Maintenance' 
AND n3.top_category = 'Personal Care Services' 
AND n4.top_category = 'Religious Organizations'
AND n5.top_category = 'Restaurants and Other Eating Places' 

