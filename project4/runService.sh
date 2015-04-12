ant clean
ant build
rm -rf /var/lib/tomcat7/webapps/eBay
rm /var/lib/tomcat7/webapps/eBay.war
cp build/eBay.war /var/lib/tomcat7/webapps
#sudo /etc/init.d/tomcat7 restart
