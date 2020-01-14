from concurrent.futures import ProcessPoolExecutor
from multiprocessing import Process
import time


def consumer(seconds: int = 5):
    print(f"Seconds={seconds}")
    time.sleep(seconds)
    print("Woke up")


if __name__ == "__main__":
    print("Started...")

    wait_time = 10
    timeout = 50
    _process = Process(target=consumer, kwargs={"seconds": wait_time})
    _process.start()

    if timeout > 0:
        _process.join(timeout)
        print(_process.exitcode)
        if _process.exitcode is None:
            _process.terminate()
            print(_process.exitcode)
            raise TimeoutError
        else:
            _process.join()

