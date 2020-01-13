from crossflow.concurrency.sleeper import SleeperBase

from crossflow.concurrency.result import Result


class Sleeper(SleeperBase):

    def __init__(self):
        super().__init__()

    def consumeSleepTimes(self, sleep_time: SleepTime):
        print(f"{self.workflow.name}:Sleeper:consumeSleepTimes received {sleep_time}")
        
        # Do work here
        
        results_result = Result()
        # modify your result here
        self.sendToResults(results_result)
