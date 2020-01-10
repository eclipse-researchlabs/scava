from builtins import ValueError
from enum import Enum, auto
import json
import os
from pathlib import Path
import unittest

from crossflow import serialization
from crossflow.runtime import (
    LogMessage,
    TaskStatus,
    TaskStatuses,
    ControlSignals,
    LogLevel,
    ControlSignal,
    Mode,
    Job,
    InternalException,
    FailedJob,
)


def json_from_file(file: str) -> str:
    with open(
        os.path.join(
            Path(os.path.dirname(os.path.realpath(__file__))),
            "../serialization/json",
            file,
        )
    ) as f:
        return f.read()


class SerializationTestEnum(Enum):
    VALUE_A = auto()
    VALUE_B = auto()


class SerializationTestObject:
    def __init__(self):
        self._string_prop: str = None
        self._int_prop: int = 0
        self._long_prop: int = 0
        self._boolean_prop: bool = True
        self._list_prop: list = []
        self._map_prop: dict = {}
        self._enum_prop: SerializationTestEnum = None

    @property
    def string_prop(self) -> str:
        return self._string_prop

    @string_prop.setter
    def string_prop(self, string_prop: str):
        self._string_prop = string_prop

    @property
    def int_prop(self) -> int:
        return self._int_prop

    @int_prop.setter
    def int_prop(self, int_prop: int):
        self._int_prop = int_prop

    @property
    def long_prop(self) -> int:
        return self._long_prop

    @long_prop.setter
    def long_prop(self, long_prop: type):
        self._long_prop = long_prop

    @property
    def boolean_prop(self) -> bool:
        return self._boolean_prop

    @boolean_prop.setter
    def boolean_prop(self, boolean_prop: type):
        self._boolean_prop = boolean_prop

    @property
    def list_prop(self) -> list:
        return self._list_prop

    @list_prop.setter
    def list_prop(self, list_prop: type):
        self._list_prop = list_prop

    @property
    def map_prop(self) -> dict:
        return self._map_prop

    @map_prop.setter
    def map_prop(self, map_prop: type):
        self._map_prop = map_prop

    @property
    def enum_prop(self) -> SerializationTestEnum:
        return self._enum_prop

    @enum_prop.setter
    def enum_prop(self, enum_prop: type):
        self._enum_prop = enum_prop

    def __eq__(self, other):
        if isinstance(other, SerializationTestObject):
            return (
                self.string_prop == other.string_prop
                and self.int_prop == other.int_prop
                and self.long_prop == other.long_prop
                and self.boolean_prop == other.boolean_prop
                and self.list_prop == other.list_prop
                and self.map_prop == other.map_prop
                and self.enum_prop == other.enum_prop
            )
        else:
            return False

    @staticmethod
    def get_primitive_instance():
        sto = SerializationTestObject()
        sto.string_prop = "default"
        sto.int_prop = 123
        sto.long_prop = 123
        sto.boolean_prop = True
        return sto

    @staticmethod
    def get_list_instance():
        parent = SerializationTestObject()
        parent.string_prop = "parent"
        parent.list_prop = []

        child_1 = SerializationTestObject()
        child_1.string_prop = "child1"
        parent.list_prop.append(child_1)

        child_2 = SerializationTestObject()
        child_2.string_prop = "child2"
        parent.list_prop.append(child_2)

        return parent

    @staticmethod
    def get_map_instance():
        parent = SerializationTestObject()
        parent.string_prop = "parent"

        child_1 = SerializationTestObject()
        child_1.string_prop = "child1"
        parent.map_prop["child1_key"] = child_1

        child_2 = SerializationTestObject()
        child_2.string_prop = "child2"
        parent.map_prop["child2_key"] = child_2

        return parent


class TestSerializerUtils(unittest.TestCase):
    def test_to_type(self):
        self.assertEqual(serialization._to_type("a string"), str)
        self.assertEqual(serialization._to_type(str), str)

    def test_sanitize_key(self):
        self.assertEqual(serialization._sanitize_key("akey"), "akey")
        self.assertEqual(serialization._sanitize_key("_a_key"), "aKey")
        self.assertEqual(serialization._sanitize_key("__a_key"), "__a_key")


class TestJsonSerializer(unittest.TestCase):
    def assertJsonEqual(self, first, second, msg=None):
        f = json.loads(first)
        s = json.loads(second)
        self.assertEqual(json.loads(first), json.loads(second), msg)

    def setUp(self):
        self.serializer = serialization.JsonSerializer()
        self.serializer.register_type(SerializationTestObject)
        self.serializer.register_type(SerializationTestEnum)

        # TODO: commented out types are currently only available in Java
        self.serializer.register_type(FailedJob)
        self.serializer.register_type(InternalException)
        self.serializer.register_type(Job)
        # self.serializer.register_type(LoggingStrategy)
        self.serializer.register_type(Mode)

        self.serializer.register_type(ControlSignal)
        self.serializer.register_type(ControlSignals)
        self.serializer.register_type(LogLevel)
        self.serializer.register_type(LogMessage)
        # self.serializer.register_type(StreamMetadata)
        # self.serializer.register_type(StreamMetadataSnapshot)
        self.serializer.register_type(TaskStatus)
        self.serializer.register_type(TaskStatuses)

    def test_serialize_should_return_correct_json_string_when_given_registered_type_with_just_primitives(
        self,
    ):
        actual = self.serializer.serialize(
            SerializationTestObject.get_primitive_instance()
        )
        expected = json_from_file("SerializationTestObject-primitives.json")
        self.assertJsonEqual(actual, expected)

    def test_serialize_should_return_correct_json_string_when_given_object_with_list(
        self,
    ):
        actual = self.serializer.serialize(SerializationTestObject.get_list_instance())
        expected = json_from_file("SerializationTestObject-list.json")
        self.assertJsonEqual(actual, expected)

    def test_serialize_should_return_correct_json_string_when_given_object_with_map(
        self,
    ):
        actual = self.serializer.serialize(SerializationTestObject.get_map_instance())
        expected = json_from_file("SerializationTestObject-map.json")
        self.assertJsonEqual(actual, expected)

    def test_serialize_should_return_correct_json_string_when_given_object_with_enum(
        self,
    ):
        obj = SerializationTestObject.get_primitive_instance()
        obj.enum_prop = SerializationTestEnum.VALUE_B
        actual = self.serializer.serialize(obj)
        expected = json_from_file("SerializationTestObject-withEnum.json")
        self.assertJsonEqual(actual, expected)

    def test_deserialize_should_return_SerializationTestObject_when_given_valid_json(
        self,
    ):
        json_str = json_from_file("SerializationTestObject-primitives.json")
        actual = self.serializer.deserialize(json_str)
        expected = SerializationTestObject.get_primitive_instance()
        self.assertEqual(actual, expected)

    def test_deserialize_should_return_SerializationTestObject_when_given_valid_json_with_list(
        self,
    ):
        json_str = json_from_file("SerializationTestObject-list.json")
        actual = self.serializer.deserialize(json_str)
        expected = SerializationTestObject.get_list_instance()
        self.assertEqual(actual, expected)

    def test_deserialize_should_return_SerializationTestObject_when_given_valid_json_with_map(
        self,
    ):
        json_str = json_from_file("SerializationTestObject-map.json")
        actual = self.serializer.deserialize(json_str)
        expected = SerializationTestObject.get_map_instance()
        self.assertEqual(actual, expected)

    def test_deserialize_should_return_SerializationTestObject_when_given_valid_json_with_enum(
        self,
    ):
        json_str = json_from_file("SerializationTestObject-withEnum.json")
        actual = self.serializer.deserialize(json_str)
        expected = SerializationTestObject.get_primitive_instance()
        expected.enum_prop = SerializationTestEnum.VALUE_B
        self.assertEqual(actual, expected)

    def test_serialize_should_return_correct_json_when_given_ControlSignal_objects(
        self,
    ):
        senderId = "JsonSerializerTest Sender"
        for signal in ControlSignals:
            with self.subTest():
                obj = ControlSignal(signal=signal, sender_id=senderId)
                actual = self.serializer.serialize(obj)
                expected = json_from_file(f"ControlSignal-{signal.name}.json")
                self.assertJsonEqual(actual, expected)

    def test_deserialize_should_return_ControlSignal_when_given_valid_json(self):
        senderId = "JsonSerializerTest Sender"
        for signal in ControlSignals:
            with self.subTest():
                json_str = json_from_file(f"ControlSignal-{signal.name}.json")
                actual = self.serializer.deserialize(json_str)
                expected = ControlSignal(signal=signal, sender_id=senderId)
                self.assertEqual(actual, expected)

    def test_serialize_should_return_correct_json_when_given_LogMessage_objects(self):
        timestamp = 123456
        instance_id = "JsonSerializerTest Sender"
        workflow = "workflow 1"
        task = "task a"
        message = "this is a message"

        for level in LogLevel:
            with self.subTest():
                logMessage = LogMessage(
                    level=level,
                    instance_id=instance_id,
                    workflow=workflow,
                    task=task,
                    message=message,
                    timestamp=timestamp,
                )
                actual = self.serializer.serialize(logMessage)
                expected = json_from_file(f"LogMessage-{level.name}.json")
                self.assertJsonEqual(actual, expected)

    def test_deserialize_should_return_LogMessage_when_given_valid_json(self):
        timestamp = 123456
        instance_id = "JsonSerializerTest Sender"
        workflow = "workflow 1"
        task = "task a"
        message = "this is a message"

        for level in LogLevel:
            with self.subTest():
                json_str = json_from_file(f"LogMessage-{level.name}.json")
                actual = self.serializer.deserialize(json_str)
                expected = LogMessage(
                    level=level,
                    instance_id=instance_id,
                    workflow=workflow,
                    task=task,
                    message=message,
                    timestamp=timestamp,
                )
                self.assertEqual(actual, expected)

    def test_serialize_should_return_correct_json_when_given_TaskStatus_objects(self):
        caller = "JsonSerializerTest Sender"
        reason = "A reason string"

        for status in TaskStatuses:
            with self.subTest():
                obj = TaskStatus(status, caller, reason)
                actual = self.serializer.serialize(obj)
                expected = json_from_file(f"TaskStatus-{status.name}.json")
                self.assertJsonEqual(actual, expected)

    def test_deserialize_should_return_TaskStatus_when_given_valid_json(self):
        caller = "JsonSerializerTest Sender"
        reason = "A reason string"

        for status in TaskStatuses:
            with self.subTest():
                json_str = json_from_file(f"TaskStatus-{status.name}.json")
                actual = self.serializer.deserialize(json_str)
                expected = TaskStatus(status, caller, reason)
                self.assertEqual(actual, expected)

    def test_serialize_should_return_correct_json_when_given_python_InternalException_objects(
        self,
    ):
        # Construct nested exception
        exception = ValueError("A value error message")
        exception.__cause__ = ValueError("A nested value error message")

        sender_id = "JsonSerializerTest Sender"
        internal_exception = InternalException.from_exception(exception, sender_id)
        actual = self.serializer.serialize(internal_exception)
        expected = json_from_file("InternalException-python.json")
        self.assertJsonEqual(actual, expected)

    def test_deserialize_should_return_InternalException_when_given_valid_json_for_python_exception(
        self,
    ):
        json_str = json_from_file("InternalException-python.json")
        actual = self.serializer.deserialize(json_str)
        reason = "ValueError: A value error message"
        stacktrace = "ValueError: A nested value error message\n\nThe above exception was the direct cause of the following exception:\n\nValueError: A value error message"
        sender_id = "JsonSerializerTest Sender"
        self.assertEqual(actual.reason, reason)
        self.assertEqual(actual.stacktrace, stacktrace)
        self.assertEqual(actual.sender_id, sender_id)
