gnome-terminal -e '
	sh -c "
	cd /home/handddle/.programs/farm-manager/ ;
	java -jar target/farm-master-0.0.1-SNAPSHOT.jar ;
	exec bash"
'

gnome-terminal -e '
	sh -c "
	cd /home/handddle/.programs/farm-manager/code_python/ ;
	python3 farm-manager.py ;
	exec bash"
'

/opt/google/chrome/chrome https://gryp3d.handddle.com/monitoring