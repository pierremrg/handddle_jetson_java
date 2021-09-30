import time
import random
import json

def generateData():

	temp = 65

	for i in range(10):

		diff = random.randint(-3, 3)
		temp += diff

		file_data = {
			'received_data': {
				'printer1': {
					'temperature': temp
				}
			}
		}

		with open('/home/pierre/eclipse-workspace/farm-master/data/artificial_{}.json'.format(i), 'w') as outfile:
			json.dump(file_data, outfile)

		print('New temperature: {}'.format(temp))
		time.sleep(3)





if __name__ == '__main__':
	generateData()