import re

import SourceModel.SM_Constants as SMCONSTS
import SourceModel.SM_Element
from SmellDetector import Utilities


class SM_PackageResource(SourceModel.SM_Element.SM_Element):
    def __init__(self, text):
        self.resourceText = text
        super().__init__(text)

    def getUsedVariables(self):
        return super().getUsedVariables()

    def getPhysicalResourceDeclarationCount(self):
        pkg_count = 0
        compiledRE = re.compile(r'\'.+\'\W*:|\".+\":')
        tempVar = compiledRE.findall(self.resourceText)
        # Utilities.myPrint("Found package declarations: " + str(tempVar))
        if (str(tempVar).replace("\"", "").replace("'", "").replace("[", "").replace("]", "").replace(":",
                                                                                                      "").strip() != ""):
            print(
                "package:" + str(tempVar).replace("\"", "").replace("'", "").replace("[", "").replace("]", "").replace(
                    ":", "").strip())
        pkg_count = len(tempVar)
        # Find list type package declarations
        compiledRE = re.compile(r'{\[((\".+?\"),?)+\]:\s*}')
        result = compiledRE.finditer(self.resourceText)
        all_pkgs = ""
        for m in result:
            all_pkgs = m.group(1)
        pkgs = all_pkgs.split(',')
        for pkg in pkgs:
            if (str(pkg).replace("\"", "").strip() != ""):
                print("package:" + str(pkg).replace("\"", "").strip())
        pkg_count += len(pkgs)
        return pkg_count

    def getResourceName(self):
        match = re.search(SMCONSTS.PACKAGE_GROUP_REGEX, self.resourceText)
        name =""
        if match:
            name = match.group(1)
        return str(name)

    def getDependentResource(self):
        resultList = []
        self.getDependentResource_(resultList, SMCONSTS.DEPENDENT_PACKAGE, SMCONSTS.DEPENDENT_GROUP_PACKAGE, SMCONSTS.PACKAGE)
        self.getDependentResource_(resultList, SMCONSTS.DEPENDENT_SERVICE, SMCONSTS.DEPENDENT_GROUP_SERVICE, SMCONSTS.SERVICE)
        self.getDependentResource_(resultList, SMCONSTS.DEPENDENT_FILE, SMCONSTS.DEPENDENT_GROUP_FILE, SMCONSTS.FILE)
        self.getDependentResource_(resultList, SMCONSTS.DEPENDENT_CLASS, SMCONSTS.DEPENDENT_GROUP_CLASS, SMCONSTS.CLASS)

        return resultList

    def getDependentResource_(self, resultList, regex, groupRegex, entity):
        compiledRE = re.compile(regex)
        for depItem in compiledRE.findall(self.resourceText):
            match = re.search(groupRegex, depItem)
            if match:
                name = match.group(1)
                resultList.append((name, entity))