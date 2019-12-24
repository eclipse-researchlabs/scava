import argparse
import sys
import time

from org.eclipse.scava.crossflow.runtime import Mode
from org.eclipse.scava.crossflow.examples.wordcount.xtext.WordCountWorkflow import WordCountWorkflow


if __name__ == '__main__':
	parser = argparse.ArgumentParser()
	parser.add_argument('-name', default='WordCountWorkflowPython', help='The name of the workflow')
	parser.add_argument('-master', default='localhost', help='IP of the master')
	parser.add_argument('-brokerHost', default='localhost', help='IP of the broker')
	parser.add_argument('-stomp', default=61613, help='Port to use for stomp based messages')
	parser.add_argument('-instance', default=None, help='The instance of the master (to contribute to)')
	parser.add_argument('-mode', default=Mode.WORKER, help='Must be master_bare, master or worker')
	
	parsedArgs = parser.parse_args(sys.argv[1:len(sys.argv)])
	
	app = WordCountWorkflow()
	app.name = parsedArgs.name
	app.master = parsedArgs.master
	app.brokerHost = parsedArgs.brokerHost
	app.stomp = parsedArgs.stomp
	app.instanceId = parsedArgs.instance
	app.mode = parsedArgs.mode
	app.run();
	while (not app.hasTerminated()):
		time.sleep(0.1)
	print("terminated")
