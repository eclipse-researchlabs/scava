'''
Created on 26 Mar 2019

@author: stevet
'''
import uuid
import stomp
import traceback
import tempfile
import pickle
import hashlib
import time
import shutil
import csv
import os
import sys

from enum import Enum
from pathlib import Path

import org.eclipse.scava.crossflow.utils.basic_xstream as bxstream

illegal_chars = [ 34, 60, 62, 124, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 58, 42, 63, 92, 47 ].sort()

currentTimeMillis = lambda: int(round(time.time() * 1000))

DEBUG = False


def debug(debugStr):
    if DEBUG:
        print(debugStr)

'''
Created on 22 Feb 2019

@author: stevet
'''
            

class CloneUtils(object):
    '''
    classdocs
    '''

    def __init__(self):
        '''
        Constructor
        '''
        
    def getUniqueRepoFolderName(self, repoUrl):
        return self.getUniqueRepoFolderNameWithName(self.extractGhRepoName(repoUrl), repoUrl)
    
    def getUniqueRepoFolderNameWithName(self, name, url):
        ret = self.cleanFileName(name)
        # create unique id for the remote url
        messageDigest = None
        try:
            messageDigest = hashlib.sha1()
            messageDigest.update(bytes(url, 'utf-8'))
            ret = ret + "-" + messageDigest.hexdigest()
        except Exception as ex:
            traceback.print_exc()
        return ret

    def cleanFileName(self, badFileName):
        cleanName = ''
        for c in badFileName:
            if not c in illegal_chars:
                cleanName += c

        # remove names ending with one or more "." / " " as this is illegal as
        # well in JGit.
        while cleanName.endswith(".") or cleanName.endswith(" "):
            cleanName = cleanName[0:len(cleanName) - 1]

        return cleanName
    
    '''
     * 
     * @param repoUrl in format "https://github.com/repoOwner/repoName"
     * @return String repoName
    '''

    def extractGhRepoName(self, repoUrl):
        repoNameStartIndex = repoUrl.find("/", 19) + 1;
        return repoUrl[repoNameStartIndex:len(repoUrl)]            
    
    '''
     * 
     * @param repoUrl in format "https://github.com/repoOwner/repoName"
     * @return String repoOwner
    '''

    def extractGhRepoOwner(self, repoUrl):
        repoNameStartIndex = repoUrl.find("/", 19) + 1;    
        return repoUrl[19:repoNameStartIndex - 1]
    
    def removeRepoClones(self, repoLocationFile):
        # clean local clone parent destination if it exists
        if (os.path.isfile(repoLocationFile)): 
            shutil.rmtree(repoLocationFile)
            print("Successfully cleaned repo clone parent: " + repoLocationFile)
            os.makedirs(repoLocationFile)
    
'''    
if __name__ == '__main__':
    url = 'https://github.com/eclipse/epsilon'
    cl = CloneUtils()
    print(cl.extractGhRepoName(url))
    print(cl.extractGhRepoOwner(url))
''' 
    
'''
Created on 22 Feb 2019

@author: stevet
'''


class ControlSignals(Enum):
    TERMINATION = 1 
    ACKNOWLEDGEMENT = 2 
    WORKER_ADDED = 3 
    WORKER_REMOVED = 4
    
    def __str__(self, *args, **kwargs):
        return self.name
    
    @staticmethod
    def enum_from_name(name):
        if name == 'TERMINATION':
            return ControlSignals.TERMINATION
        elif name == 'ACKNOWLEDGEMENT':
            return ControlSignals.ACKNOWLEDGEMENT
        elif name == 'WORKER_ADDED':
            return ControlSignals.WORKER_ADDED
        else:
            return ControlSignals.WORKER_REMOVED

    
class ControlSignal(object):
    '''
    classdocs
    '''

    def __init__(self, controlSignal=ControlSignals.TERMINATION, senderId=''):
        '''
        Constructor
        '''
        self.signal = controlSignal
        self.senderId = senderId

'''
Created on 23 Feb 2019

@author: stevet
'''


class CSVParser(object):
    '''
    classdocs
    '''

    def __init__(self, path):
        '''
        Constructor
        '''
        self.parser = csv.DictReader(path)
        
    def getRecordsIterable(self):
        return self.parser
    
    def getRecordsList(self):
        return list(self.parser)

'''
Created on 23 Feb 2019

@author: stevet
'''


class CSVWriter(object):
    '''
    classdocs
    '''

    def __init__(self, filePath, headers):
        '''
        Constructor
        '''
        if (not os.path.isfile(filePath)): 
            parent = filePath[0:filePath.rfind("/") - 1]
            os.mkdir(parent)
        self.writer = csv.writer(filePath)
        self.writer.writerow(headers)
        
    def writeRecord(self, record):
        self.writer.writerow(record)
        
    def flush(self):
        self.writer.flush()
        
    def close(self):
        self.writer.close()

'''
Created on 27 Feb 2019

@author: stevet
'''

    
class QueueType(Enum):
    QUEUE = 1 
    TOPIC = 2 

    def __str__(self, *args, **kwargs):
        return self.name
    
    @staticmethod
    def enum_from_name(name):
        if name == 'QUEUE':
            return QueueType.QUEUE
        else:
            return QueueType.TOPIC


def str2QueueType(qTypeStr):
    if qTypeStr.upper() == 'QUEUE':
        return QueueType.QUEUE
    return QueueType.TOPIC


class QueueInfo(object):
    '''
    classdocs
    '''

    def __init__(self, queueType, queueName, prefetchSize=0):
        '''
        Constructor
        '''
        self.queueType = queueType
        self.queueName = queueName
        self.prefetchSize = prefetchSize
        
    def isTopic(self):
        return self.queueType == QueueType.TOPIC
    
    def isQueue(self):
        return self.queueType == QueueType.QUEUE
    
    def getStompDestinationName(self):
        if self.isQueue():
            return '/queue/' + self.queueName
        else:
            return '/topic/' + self.queueName
        
    def getPrefetchSize(self):
        return self.prefetchSize
        
'''
Created on 23 Feb 2019

@author: stevet
'''


class Stream(object):

    def __init__(self, name, size, inFlight, isTopic, numberOfSubscribers):
        '''
        Constructor
        '''
        self.name = name
        self.size = size
        self.inFlight = inFlight
        self.isTopic = isTopic
        self.numberOfSubscribers = numberOfSubscribers
    
    def getName(self):
        return self.name
    
    def getSize(self):
        return self.size
    
    def getInFlight(self):
        return self.inFlight
    
    def getIsTopic(self):
        return self.isTopic
    
    def getNumberOfSubscribers(self):
        return self.numberOfSubscribers
    
    def setNumberOfSubscribers(self, numberOfSubscribers):
        self.numberOfSubscribers = numberOfSubscribers

     
class StreamMetadata(object):
    '''
    classdocs
    '''
    
    def __init__(self):
        self.streams = set()
   
    def addStream(self, name, size, inFlight, isTopic, l):
        stream = Stream(name, size, inFlight, isTopic, l)
        sizeBefore = len(self.streams)
        self.streams.add(stream)
        return not (sizeBefore == len(self.streams))

    def getStreams(self):
        return self.streams

    def getStream(self, name):
        for s in self.streams:
            if(s.name == name):
                return s

    def pruneNames(self, length):
        for s in self.streams:
            if (len(s.name) >= length):
                s.name = s.name[0:length]
            elif (len(s.name) < length):
                s.name = ("%-" + length + "s") % s.name
    
    def __str__(self, *args, **kwargs):
        ret = "Stream Metadata at epoch: " + int(round(time.time() * 1000)) + "\r\n"
        for s in self.streams:
            ret = ret + s.name + "\tsize: " + s.size + "\t: " + s.inFlight + "\tisTopic: " + s.isTopic + \
                    "\tnumberOfSubscribers: " + s.numberOfSubscribers + "\r\n"
        return ret

'''
Created on 23 Feb 2019

@author: stevet
'''


class TaskStatuses(Enum):
    STARTED = 1 
    WAITING = 2 
    INPROGRESS = 3 
    BLOCKED = 4
    FINISHED = 5
    
    def __str__(self, *args, **kwargs):
        return self.name

    @staticmethod
    def enum_from_name(name):
        if name == 'STARTED':
            return TaskStatuses.STARTED
        elif name == 'WAITING':
            return TaskStatuses.WAITING
        elif name == 'INPROGRESS':
            return TaskStatuses.INPROGRESS
        elif name == 'BLOCKED':
            return TaskStatuses.BLOCKED
        else:
            return TaskStatuses.FINISHED

    
class TaskStatus(object):
    '''
    classdocs
    '''

    def __init__(self, status=TaskStatuses.STARTED, caller='', reason=''):
        '''
        Constructor
        '''
        self.status = status
        self.caller = caller
        self.reason = reason

    def getStatus(self):
        return self.status

    def getCaller(self):
        return self.caller

    def getReason(self):
        return self.reason
    
    def __str__(self, *args, **kwargs):
        return str(self.status) + " | caller: " + str(self.caller) + " reason: " + str(self.reason)

'''    
if __name__ == '__main__':
    testVal = TaskStatuses.WAITING
    
    print('stat='+str(testVal))
'''

'''
Created on 13 Mar 2019

@author: stevet
'''


class Mode(Enum):
    MASTER_BARE = 1 
    MASTER = 2 
    WORKER = 3 
    
    def __str__(self, *args, **kwargs):
        return self.name

    @staticmethod
    def enum_from_name(name):
        if name == 'MASTER_BARE':
            return Mode.MASTER_BARE
        elif name == 'MASTER':
            return Mode.MASTER
        else:
            return Mode.WORKER


def convert(modeString):
    if('master' == modeString): 
        return Mode.MASTER
    if('worker' == modeString): 
        return Mode.WORKER
    if('master_bare' == Mode.MASTER_BARE):
        raise Exception("Mode must be 'master_bare', 'master' or 'worker' but was '" + modeString + "'")

'''
Created on 27 Feb 2019

@author: stevet
'''


class Task(object):
    '''
    classdocs
    '''

    def __init__(self):
        '''
        Constructor
        '''
        self.cacheable = True
        self.resultsTopic = None
        self.subscriptionId = uuid.uuid4().int

    def getId(self):
        pass
    
    def getWorkflow(self):
        pass
    
    def setResultsTopic(self, resultsTopic):
        self.resultsTopic = resultsTopic
    
    def getResultsTopic(self):
        return self.resultsTopic
    
    def sendToResultsTopic(self, result):
        self.getResultsTopic().send(result)
    
    def isCacheable(self):
        return self.cacheable
       
    def setCacheable(self, cacheable):
        self.cacheable = cacheable
    
    def getSubscriptionId(self):
        return self.subscriptionId
    
    """
     * Call this within consumeXYZ() to denote task blocked due to some reason
     * @param reason
    """

    def taskBlocked(self, reason):
        self.getWorkflow().setTaskBlocked(reason)
    
    """
     * Call this within consumeXYZ() to denote task is now unblocked
     * @param reason
    """

    def taskUnblocked(self):
        self.getWorkflow().setTaskUnblocked(self)


class BuiltinStreamConsumer(object):
    '''
    classdocs
    '''

    def __init__(self, consumerFunc):
        '''
        Constructor
        '''
        self.consumerFunc = consumerFunc
        self.subscriptionId = uuid.uuid4().int
        
    def consume(self, streamType, ackFunc=None):
        if ackFunc == None:
            self.consumerFunc(streamType)
        else:
            self.consumerFunc(streamType, ackFunc)
    
    def getSubscriptionId(self):
        return self.subscriptionId

'''
Created on 23 Feb 2019

@author: stevet

refs:

http://activemq.apache.org/stomp.html

'''
    
import uuid


class MessageListener(stomp.ConnectionListener):

    def __init__(self, conn, consumer, workflow, destName, convertToObject=False, clientAcks=False):
        self.conn = conn
        self.consumer = consumer
        self.workflow = workflow
        self.destName = destName
        self.listenerIdUuid = uuid.uuid4()
        self.listenerId = str(self.listenerIdUuid)
        self.convertToObject = convertToObject
        self.clientAcks = clientAcks
        debug('Created listener for ' + destName)

    def on_error(self, headers, message):
        if headers['destination'] == self.destName:
            debug('received an error "%s"' % message)

    def on_message(self, headers, message):
        if headers['destination'] == self.destName:
            debug("Rx fm " + self.destName)
            debug('received a message "%s"' % message)
            ackFunc = None
            if self.clientAcks:

                def doClientAck():
                    messageId = headers['message-id']
                    subscription = headers['subscription']
                    self.conn.ack(messageId, subscription)

                ackFunc = doClientAck
            try:
                if self.convertToObject:
                    self.consumer.consume(bxstream.bxToObject(message), ackFunc)
                else:
                    self.consumer.consume(message, ackFunc)
            except Exception as inst:
                traceback.print_exc()
                exc_type, exc_value, exc_traceback = sys.exc_info()
                stack = traceback.format_exception(exc_type, exc_value, exc_traceback)
                if self.workflow != None:
                    self.workflow.reportInternalException(inst, '', stack)
                else:
                    raise inst
        else:
            print('Discarded message(' + self.destName + ') from ' + headers['destination'])

    def on_disconnected(self):
        debug('disconnected')
        
    def getListenerId(self):
        return self.listenerId
        
    def getListenerIdAsInt(self):
        return self.listenerIdUuid.int
        
 
class BuiltinStream(object):
    '''
    classdocs
    '''

    def __init__(self, workflow, name, *args):
        '''
        Constructor
        '''
        self.broadcast = True
        if(len(args) > 0):
            self.broadcast = args[0]
        self.workflow = workflow
        self.name = name
        self.consumers = []
        self.listeners = []
        self.pendingConsumers = []
        
    '''    
    protected ActiveMQDestination destination;
    protected Connection connection;
    protected Session session;
    protected Workflow workflow;
    protected List<MessageConsumer> consumers = new LinkedList<>();
    protected List<BuiltinStreamConsumer<T>> pendingConsumers = new ArrayList<>();
    protected String name;
    protected boolean broadcast;
    '''
    
    def getDestinationName(self):
        return self.name + "." + self.workflow.getInstanceId()
    
    def init(self):
        self.connection = stomp.Connection(
            host_and_ports=self.workflow.getBroker(),
            reconnect_sleep_initial=15,
            reconnect_sleep_increase=0.0,
            reconnect_sleep_jitter=0.0,
            reconnect_sleep_max=15.0,
            reconnect_attempts_max=-1
        )
        self.connection.start()
        self.connection.connect(wait=True)  # add credentials?

        self.sessionCreated = True
        # session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        self.updateDestination()
        for consumer in self.pendingConsumers:
            self.addConsumer(consumer)
        self.sessionCreated = True
        self.pendingConsumers.clear();
        
    def updateDestination(self):
        if (self.broadcast):
            self.destination = '/topic/' + self.getDestinationName()
        else: 
            self.destination = '/queue/' + self.getDestinationName()
    
    def send(self, t):
        self.updateDestination()
        messageBody = bxstream.bxToString(t)
        self.connection.send(body=messageBody, destination=self.destination)
        # producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT); ?
        # producer.setPriority(9); ?
        
    def sendSerialized(self, xmlObj):
        self.updateDestination()
        self.connection.send(body=xmlObj, destination=self.destination)

    def addConsumer(self, consumer):
        self.updateDestination()
        
        if (not self.sessionCreated):
            self.pendingConsumers.add(consumer);
            return;
        
        self.consumers.append(consumer)
        listener = MessageListener(self.connection, consumer, self.workflow, self.destination, True)
        self.listeners.append(listener)
        self.connection.set_listener(listener.getListenerId(), listener)
        self.connection.subscribe(self.destination, id=consumer.getSubscriptionId(), ack='auto')

    def stop(self):
        for c in self.consumers:
            self.connection.unsubscribe(c.getSubscriptionId())
        self.consumers = []
        
        for l in self.listeners:
            self.connection.remove_listener(l.getListenerId())
        
        self.connection.disconnect()   

    def isBroadcast(self):
        return self.broadcast

    def getDesinationNames(self):
        return [self.destination]
    
    def getName(self):
        return self.name
    
'''
Created on 25 Feb 2019

@author: stevet
'''

'''@synchronized'''


class DirectoryCache(object):
    
    def __init__(self, directory=None):
        self.jobFolderMap = {}
        self.jobMap = {}
        self.workflow = None
        self.pendingTransactions = {}
        
        if directory == None:
            self.directory = tempfile.NamedTemporaryFile(prefix='crossflow')
        else:
            self.directory = open(directory)
            
        self.directoryFullpath = os.path.realpath(self.directory.name)
    
        if (not Path(directory).is_dir()):
            return;
        
        for streamFolder in os.listdir(self.directoryFullpath):
            if not Path(streamFolder).is_dir():
                continue
            for jobFolder in os.listdir(streamFolder):
                if not Path(jobFolder).is_dir():
                    continue
                jobFolderObject = open(jobFolder)
                self.jobFolderMap[jobFolderObject.name] = jobFolderObject
    
    def getCachedOutputs(self, inputJob):
        if (self.hasCachedOutputs(inputJob)):
            outputs = []
            inputFolderObject = self.jobFolderMap.get(inputJob.getHash())
            for outputFilePath in os.listdir(os.path.realpath(inputFolderObject.name)):
                outputFile = open(outputFilePath)
                outputJob = bxstream.bxToObject(outputFile.read())
                outputFile.close() 
                outputJob.setId(str(uuid.uuid4()))
                outputJob.setCorrelationId(inputJob.getId())
                outputJob.setCached(True)
                outputs.append(outputJob)
            return outputs
        else:
            return []
    
    def hasCachedOutputs(self, inputJob):
        return inputJob.getHash() in self.jobFolderMap.keys()
    
    def cache(self, outputJob):
        if (not outputJob.isCacheable()):
            return

        self.jobMap[outputJob.getId()] = outputJob
        inputJob = self.jobMap.get(outputJob.getCorrelationId())
        
        if (not inputJob == None):
            streamFolderPath = self.directoryFullpath + "/" + inputJob.getDestination()
            try:
                inputFolderPath = streamFolderPath + "/" + inputJob.getHash()
                os.makedirs(inputFolderPath)
                with open(inputFolderPath + "/" + outputJob.getHash()) as outputFile:
                    self.jobFolderMap[inputJob.name] = inputFolderPath
                    self.save(outputJob, outputFile)
            except Exception as ex:
                traceback.print_exc()
                exc_type, exc_value, exc_traceback = sys.exc_info()
                stack = traceback.format_exception(exc_type, exc_value, exc_traceback)
                self.workflow.reportInternalException(ex, "Error caching job " + outputJob.name, stack)
    
    def getDirectory(self):
        return self.directory
    
    def save(self, job, file):
        file.write(job.getXml())
        file.flush()
    
    def setWorkflow(self, workflow):
        self.workflow = workflow;
    
    def cacheTransactionally(self, outputJob):

        if (outputJob.isTransactionSuccessMessage()):
            self.cachePendingTransactions(outputJob.getCorrelationId())
            return

        if (not outputJob.isCacheable()):
            return

        # even though the task producing this job may have failed, this job itself is
        # complete so should be indexed in the job map regardless
        self.jobMap[outputJob.getId()] = outputJob        

        if (outputJob.getCorrelationId() == None):
            return

        currentPending = self.pendingTransactions[outputJob.getCorrelationId()]
        if currentPending == None:
            currentPending = []
        currentPending.append(outputJob)

        self.pendingTransactions[outputJob.getCorrelationId()] = currentPending

    def cachePendingTransactions(self, correlationId):
        inputJob = self.jobMap[correlationId]

        if (inputJob != None):
            streamFolder = self.directoryFullpath + "/" + inputJob.getDestination()
            try:
                pending = self.pendingTransactions[correlationId]
                if (not pending == None):
                    inputFolder = streamFolder + "/" + inputJob.getHash()
                    os.makedirs(inputFolder)
                    for outputJob in pending:
                        with open(inputFolder + "/" + outputJob.getHash()) as outputFile:
                            self.jobFolderMap[inputJob.getHash()] = inputFolder
                            self.save(outputJob, outputFile)
            except Exception as ex:
                print(correlationId)
                print(self.pendingTransactions)
                traceback.print_exc()
                exc_type, exc_value, exc_traceback = sys.exc_info()
                stack = traceback.format_exception(exc_type, exc_value, exc_traceback)
                self.workflow.reportInternalException(ex, 'Error caching pending transaction for correlation id' + correlationId, stack);

'''
Created on 27 Feb 2019

@author: stevet
'''


class FailedJob(object):
    '''
    classdocs
    '''

    def __init__(self, job, exception, worker, task):
        '''
        Constructor
        '''
        self.job = job
        self.exception = exception
        self.worker = worker
        self.task = task

    def getJob(self):
        return self.job
    
    def setJob(self, job):
        self.job = job
 
    def getException(self):
        return self.exception

    def setException(self, exception):
        self.exception = exception
    
    def getWorker(self):
        return self.worker

    def setWorker(self, worker):
        self.worker = worker
    
    def getTask(self):
        return self.task

    def setTask(self, task):
        self.task = task

    def __str__(self, *args, **kwargs):
        return self.job + " | " + self.exception + " " + self.worker + " " + self.task

'''
Created on 27 Feb 2019

@author: stevet
'''


class InternalException(Exception):
    '''
    classdocs
    '''

    def __init__(self, exception=Exception('Dummy'), message=None, worker=None, stackTrace=None):
        '''
        Constructor
        '''
        self.exception = exception
        self.worker = worker
        self.message = message
        
    def getException(self):
        return self.exception
    
    def setException(self, exception):
        self.exception = exception
    
    def getWorker(self):
        return self.worker
    
    def setWorker(self, worker):
        self.worker = worker;

'''
Created on 27 Feb 2019

@author: stevet
'''


class CacheManagerTask(Task):
    
    def __init__(self, workflow):
        self.workflow = workflow
        
    def getWorkflow(self):
        return self.workflow
    
    def getId(self):
        return 'CacheManager'
    
'''
Created on 27 Feb 2019

@author: stevet
'''


class Job(object):
    '''
    classdocs
    '''

    def __init__(self):
        '''
        Constructor
        '''
        self.id = str(uuid.uuid4())
        self.correlationId = ''
        self.destination = ''
        self.cached = False
        self.failures = 0
        self.cacheable = True
        # sets whether self job requires a transactional level of caching (usually due
        # to being created multiple times per single task)
        self.transactional = True
        # denotes that self job is a simple message denoting success of a transaction
        # (with self correlationId)
        self.isTransactionSuccessMessage = False
        self.totalOutputs = 0

    def __str__(self, *args, **kwargs):
        return self.id + " " + self.correlationId + " " + self.destination + " " + self.cacheable + " " + self.failures

    def isTransactional(self):
        return self.transactional

    def setTransactional(self, transactional):
        self.transactional = transactional

    def setId(self, id):
        self.id = id;

    def getId(self):
        return self.id
    
    def setCorrelationId(self, correlationId):
        self.correlationId = correlationId

    def getCorrelationId(self):
        return self.correlationId

    def setDestination(self, destination):
        self.destination = destination
    
    def getDestination(self):
        return self.destination

    def isCached(self):
        return self.cached

    def setCached(self, cached):
        self.cached = cached

    def isCacheable(self):
        return self.cacheable

    def setCacheable(self, cacheable):
        self.cacheable = cacheable

    def getFailures(self):
        return self.failures

    def setFailures(self, failures):
        self.failures = failures

    def getPickleBytes(self):
        id = self.id
        failures = self.failures
        correlationId = self.correlationId
        cached = self.cached
        cacheable = self.cacheable

        self.id = None
        self.correlationId = None
        self.failures = 0
        self.cached = False
        self.cacheable = True

        pickleBytes = pickle.dumps(self)

        self.jobId = id;
        self.correlationId = correlationId;
        self.cached = cached;
        self.failures = failures;
        self.cacheable = cacheable;

        return pickleBytes    

    def getHash(self):
        # FIXME if two outputs have the same signature (aka if a task outputs two
        # identical elements) then duplicates are lost!
        h = hashlib.md5()
        h.update(self.getPickleBytes())
        return str(h.digest())

    def getIsTransactionSuccessMessage(self):
        return self.isTransactionSuccessMessage
    
    def setIsTransactionSuccessMessage(self, isTransactionSuccessMessage):
        self.isTransactionSuccessMessage = isTransactionSuccessMessage

        
class JobStream(Job):
    '''
    classdocs
    '''

    def __init__(self, workflow):
        '''
        Constructor
        '''
        self.workflow = workflow
        self.destination = {}  # taskId : QueueInfo
        self.preQueue = {}  # taskId : QueueInfo
        self.postQueue = {}  # taskId : QueueInfo
        self.rxConnections = {}
        self.txConnection = stomp.Connection(self.workflow.getBroker())
        self.txConnection.start()
        self.txConnection.connect(wait=True)  # add credentials?
        self.consumers = []
        self.cacheManagerTask = CacheManagerTask(workflow)
    
    # TODO should probably make this thread safe
    def sendMessage(self, msg, dest):
        debug('Tx to ' + dest)
        self.txConnection.send(body=msg, destination=dest)
        
    def getRxConnection(self, dest):
        if dest in self.rxConnections:
            return self.rxConnections[dest]
        connection = stomp.Connection(self.workflow.getBroker())
        connection.start()
        connection.connect(wait=True)  # add credentials?
        self.rxConnections[dest] = connection
        return connection
    
    def subscribe(self, queueInfo, msgCallbackFunc):
        stompDestName = queueInfo.getStompDestinationName()
        stompHeaders = {}
        ackMode = 'auto'
        if queueInfo.getPrefetchSize() > 0:
            stompHeaders['activemq.prefetchSize'] = queueInfo.getPrefetchSize()
            ackMode = 'client'
        print('Subscribe ' + self.workflow.getName() + ' to ' + stompDestName)
        consumer = BuiltinStreamConsumer(msgCallbackFunc)
        connection = self.getRxConnection(stompDestName)
        listener = MessageListener(connection, consumer, self.workflow, stompDestName, False, ackMode == 'client')
        connection.set_listener(listener.getListenerId(), listener)
        connection.subscribe(stompDestName, consumer.getSubscriptionId(), ack=ackMode, headers=stompHeaders)
        self.consumers.append(consumer)

    def send(self, job, taskId):
        try:
            dest = None
            # if the sender is one of the targets of this stream, it has re-sent a message
            # so it should only be put in the relevant physical queue
            job.setDestination(bxstream.getSimpleClassName(self))
            msgBody = bxstream.bxToString(job)
            dest = self.preQueue.get(taskId, None)
            if (dest != None):
                # producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT); - stomp is NON_PERSISTENT by default
                stompDest = dest.getStompDestinationName()
                self.sendMessage(msgBody, stompDest)
            else:
                # otherwise the sender must be the source of this stream so intends to
                # propagate its messages to all the physical queues
                for taskId in self.preQueue.keys():
                    stompDest = self.preQueue[taskId].getStompDestinationName()
                    self.sendMessage(msgBody, stompDest)
        except Exception as ex:
            traceback.print_exc()
            exc_type, exc_value, exc_traceback = sys.exc_info()
            stack = traceback.format_exception(exc_type, exc_value, exc_traceback)
            self.workflow.reportInternalException(ex, '', stack);
    
    def getDestinationNames(self):
        return map(lambda x : x.getStompDestinationName(), self.dest.keys())
        
    def stop(self):
        self.txConnection.stop()
        for rxConKey in self.rxConnections:
            self.rxConnections[rxConKey].stop()

    def isBroadcast(self):
        return self.destination.values().next().isTopic()
    
    def getAllQueues(self):
        ret = map(lambda x : x.queueName, self.preQueue.values())
        ret.extend(map(lambda x : x.queueName, self.postQueue.values()))
        ret.extend(map(lambda x : x.queueName, self.destination.values()))
        return ret

'''
Created on 13 Mar 2019

@author: stevet
'''

'''@synchronized'''


class Workflow(object):
    '''
    classdocs
    '''

    def __init__(self,
                 name='',
                 cache=None,
                 master='localhost',
                 brokerHost='localhost',
                 stompPort=61613,
                 instanceId=None,
                 mode=Mode.WORKER,
                 cacheEnabled=True,
                 deleteCache=None):
        '''
        Constructor
        '''        
        self.name = name
        self.cache = cache
        self.master = master
        self.brokerHost = brokerHost
        self.stompPort = stompPort

        self.instanceId = instanceId
        if instanceId is None:
            self.instanceId = str(uuid.uuid4())

        self.mode = mode
        self.cacheEnabled = cacheEnabled

        # TODO: REMOVE THIS, KEPT IN UNTIL CODE REFACTOR CAN BE DONE NOT NEEDED UNLESS PYTHON MASTER REQUIRED
        self.createBroker = True

        self.cacheEnabled = True
        self.activeJobs = []
        self.activeStreams = []
        self.terminated = False

        self.inputDirectory = ''
        self.outputDirectory = ''
        self.tempDirectory = None

        self.taskStatusTopic = BuiltinStream(self, "TaskStatusPublisher")
        self.resultsTopic = BuiltinStream(self, "ResultsBroadcaster")
        self.streamMetadataTopic = BuiltinStream(self, "StreamMetadataBroadcaster")
        self.controlTopic = BuiltinStream(self, "ControlTopic")
        self.failedJobsQueue = BuiltinStream(self, "FailedJobs", False)
        self.internalExceptionsQueue = BuiltinStream(self, "InternalExceptions", False)

        self.failedJobs = None
        self.internalExceptions = None

        # for master to keep track of active and terminated workers
        self.activeWorkerIds = []
        self.terminatedWorkerIds = []

        # excluded tasks from workers
        self.tasksToExclude = []
        self.delay = 0
        self.terminationTimer = None
        self.streamMetadataTimer = None

        """
         * Sets whether tasks are able to obtain more jobs while they are in the middle
         * of processing one already
        """
        self.enablePrefetch = False
        # terminate workflow on master after this time (ms) regardless of confirmation
        # from workers
        self.terminationTimeout = 10000
        
        print("Workflow initialised.")
    
    def excludeTasks(self, tasks):
        self.tasksToExclude = tasks

    def isCreateBroker(self):
        return self.createBroker

    def setCreateBroker(self, createBroker):
        self.createBroker = createBroker

    """
     * used to manually add local workers to master as they may be enabled too
     * quickly to be registered using the control topic when on the same machine
    """

    def addActiveWorkerId(self, workerId):
        self.activeWorkerIds.add(workerId)

    def setTerminationTimeout(self, timeout):
        self.terminationTimeout = timeout

    def getTerminationTimeout(self):
        return self.terminationTimeout
    
    def consumeControlSignal(self, signal):
        if (self.isMaster()): 
            sig = signal.signal
            if sig == ControlSignals.ACKNOWLEDGEMENT:
                self.terminatedWorkerIds.append(signal.senderId)
            elif sig == ControlSignals.WORKER_ADDED:
                self.activeWorkerIds.append(signal.senderId)
            elif sig == ControlSignals.WORKER_REMOVED:
                self.activeWorkerIds.remove(signal.senderId)
        else: 
            if signal.signal == ControlSignals.TERMINATION:
                try: 
                    self.terminate()
                except Exception as ex:
                    traceback.print_exc()
                    exc_type, exc_value, exc_traceback = sys.exc_info()
                    stack = traceback.format_exception(exc_type, exc_value, exc_traceback)
                    self.unrecoverableException(ex, stack)
                    
    def consumeTaskStatus(self, task):
        status = task.status
        
        if status == TaskStatuses.INPROGRESS: 
            self.activeJobs.append(task.caller)
            self.cancelTermination()
        elif status == TaskStatuses.WAITING: 
            self.activeJobs.remove(task.caller)
            
    def consumeFailedJob(self, failedJob):
        print(failedJob.getException())
        self.failedJobs.append(failedJob)
            
    def consumeInternalException(self, internalException):
        print(internalException.getException())
        self.internalExceptions.append(internalException)
        
    def connect(self): 
        if (self.tempDirectory == None): 
            self.tempDirectory = tempfile.NamedTemporaryFile(prefix='crossflow')
        self.taskStatusTopic.init()
        self.resultsTopic.init()
        self.streamMetadataTopic.init()
        self.controlTopic.init()
        self.failedJobsQueue.init()
        self.internalExceptionsQueue.init()
        self.activeStreams.append(self.taskStatusTopic)
        self.activeStreams.append(self.failedJobsQueue)
        self.activeStreams.append(self.internalExceptionsQueue)
        # XXX do not add this topic/queue or any other non-essential ones to
        # activestreams as the workflow should be able to terminate regardless of their
        # state
        # activeStreams.add(resultsTopic);
        # activeStreams.add(controlTopic);
        # activeStreams.add(streamMetadataTopic);
        self.controlTopic.addConsumer(BuiltinStreamConsumer(self.consumeControlSignal)) 
        # XXX if the worker sends this before the master is listening to this topic
        # / this information is lost which affects termination

        if self.isMaster(): 
            self.taskStatusTopic.addConsumer(BuiltinStreamConsumer(self.consumeTaskStatus)) 
            self.failedJobs = []
            self.failedJobsQueue.addConsumer(BuiltinStreamConsumer(self.consumeFailedJob))
            self.internalExceptions = []
            self.internalExceptionsQueue.addConsumer(BuiltinStreamConsumer(self.consumeInternalException))

            # terminationTimer = new Timer(); TODO
        else:
            self.controlTopic.send(ControlSignal(ControlSignals.WORKER_ADDED, self.getName()))

    def cancelTermination(self): 
        self.aboutToTerminate = False

    def getName(self):
        return self.name

    def setName(self, name): 
        self.name = name

    def getInstanceId(self): 
        return self.instanceId

    def setInstanceId(self, instanceId):
        self.instanceId = instanceId

    def getCache(self):
        return self.cache

    def setCache(self, cache): 
        self.cache = cache
        cache.setWorkflow(self)

    def isMaster(self):
        return self.mode == Mode.MASTER or self.mode == Mode.MASTER_BARE

    def isWorker(self): 
        return self.mode == Mode.MASTER or self.mode == Mode.WORKER

    def getMode(self): 
        return self.mode

    def setMaster(self, master): 
        self.master = master

    def getMaster(self): 
        return self.master

    def getStompPort(self): 
        return self.stompPort

    def setStompPort(self, stompPort): 
        self.stompPort = stompPort

    def getBroker(self): 
        return [(self.brokerHost, self.stompPort)]

    def stopBroker(self): 
        self.brokerService.deleteAllMessages()
        self.brokerService.stopGracefully("", "", 1000, 1000)
        print("terminated broker (" + self.getName() + ")")

    """
     * delays the execution of sources for 'delay' milliseconds. Needs to set the
     * delay field in the superclass.
     * 
     * @param delay
     * @throws Exception
     """

    def run(self, delay=0):
        pass

    def areStreamsEmpty(self): 
        """
        TODO - possibly use jolokia?

        {noformat} 
        curl -u admin:admin http://localhost:8161/api/jolokia/read/org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName=Test1/QueueSize
        {noformat} 
        """
        return True

    def terminate(self): 
        if self.terminated:
            return

        if self.terminationTimer != None:
            self.terminationTimer.cancel()

        try: 
            # master graceful termination logic
            if self.isMaster(): 

                # ask all workers to terminate
                self.controlTopic.send(ControlSignal(ControlSignals.TERMINATION, self.getName()))

                startTime = currentTimeMillis()
                # wait for workers to terminate or for the termination timeout
                while ((currentTimeMillis() - startTime) < self.terminationTimeout): 
                    # System.out.println(terminatedWorkerIds);
                    # System.out.println(activeWorkerIds);
                    if (self.terminatedWorkerIds.equals(self.activeWorkerIds)): 
                        print("all workers terminated, terminating master...");
                        break
                    
                    # Thread.sleep(100);
                
                print("terminating master...");

            # termination logic
            print("terminating workflow... (" + self.getName() + ")");

            if self.isMaster(): 
                if(self.streamMetadataTimer != None):
                    self.streamMetadataTimer.cancel()
                try: 
                    self.streamMetadataTopic.stop()
                except Exception as ex:
                    debug(ex) 
                    # Ignore any exception
                
                self.activeStreams.remove(self.streamMetadataTopic)
                try: 
                    self.controlTopic.stop()
                except Exception as ex:
                    debug(ex) 
                    # Ignore any exception
                
                self.activeStreams.remove(self.controlTopic)
                print("createBroker: " + self.createBroker)
                if self.createBroker: 
                    self.stopBroker()
                
            else:

                try: 
                    self.controlTopic.stop()
                except Exception as ex:
                    traceback.print_exc()
                    debug(ex) 
                    # Ignore any exception
                
                self.activeStreams.remove(self.controlTopic)
            try:
                self.resultsTopic.stop()
            except Exception as ex:
                traceback.print_exc()
                debug(ex) 
                # Ignore any exception

            self.activeStreams.remove(self.resultsTopic)

            # stop all remaining stream connections
            for stream in self.activeStreams: 
                try:
                    stream.stop()
                except Exception as ex:
                    traceback.print_exc()
                    # Ignore any exception
            
            if not self.isMaster():                 
                self.controlTopic.send(ControlSignal(ControlSignals.ACKNOWLEDGEMENT, self.getName()))

            self.terminated = True
            print("workflow " + self.getName() + " terminated.")
        except Exception as ex:
            traceback.print_exc()
 
            # There is nothing to do at this stage -- print error for debugging purposes
            # only
            # ex.printStackTrace();

    def hasTerminated(self): 
        return self.terminated

    def getTaskStatusTopic(self):
        return self.taskStatusTopic

    def getResultsTopic(self): 
        return self.resultsTopic

    def getStreamMetadataTopic(self): 
        return self.streamMetadataTopic

    def getControlTopic(self): 
        return self.controlTopic

    def getFailedJobsQueue(self): 
        return self.failedJobsQueue

    def getFailedJobs(self): 
        return self.failedJobs

    def getInternalExceptions(self): 
        return self.internalExceptions

    def reportInternalException(self, ex, message, stackTrace):
        try:
            ser_obj = bxstream.pyExceptionToJavaXML(InternalException(ex, message, None, stackTrace), stackTrace)
            self.internalExceptionsQueue.sendSerialized(ser_obj)
        except Exception as ex:
            traceback.print_exc()
            exc_type, exc_value, exc_traceback = sys.exc_info()
            stack = traceback.format_exception(exc_type, exc_value, exc_traceback)
            self.unrecoverableException(ex, stack)
        
    def unrecoverableException(self, ex): 
        print(ex)

    def setTaskInProgess(self, caller): 
        self.setTaskInProgessWithReason(caller, 'reason')

    def setTaskInProgessWithReason(self, caller, reason): 
        self.taskStatusTopic.send(TaskStatus(TaskStatuses.INPROGRESS, caller.getId(), reason))

    def setTaskWaiting(self, caller): 
        self.taskStatusTopic.send(TaskStatus(TaskStatuses.WAITING, caller.getId(), ""))

    def setTaskBlocked(self, caller, reason): 
        self.taskStatusTopic.send(TaskStatus(TaskStatuses.BLOCKED, caller.getId(), reason))

    def setTaskUnblocked(self, caller): 
        self.taskStatusTopic.send(TaskStatus(TaskStatuses.INPROGRESS, caller.getId(), ""))

    def getInputDirectory(self):
        return self.inputDirectory

    def setInputDirectory(self, inputDirectory): 
        self.inputDirectory = inputDirectory

    def getOutputDirectory(self): 
        return self.outputDirectory

    def setOutputDirectory(self, outputDirectory): 
        self.outputDirectory = outputDirectory

    def getTempDirectory(self): 
        return self.tempDirectory

    def setTempDirectory(self, tempDirectory): 
        self.tempDirectory = tempDirectory

    def getSerializer(self): 
        return self.serializer

    def setStreamMetadataPeriod(self, p): 
        self.streamMetadataPeriod = p

    def getStreamMetadataPeriod(self): 
        return self.streamMetadataPeriod

    '''
     * 
     * @return A set containing all ActiveMQDestination objects used by all active JobStreams 
    '''
    '''
    public Set<ActiveMQDestination> getAllJobStreamsInternals() 
        Set<ActiveMQDestination> ret = new HashSet<ActiveMQDestination>();
        activeStreams.stream().filter(s -> s instanceof JobStream)
                .forEach(js -> ret.addAll(((JobStream<?>) js).getAllQueues()));
        return ret;
    '''

