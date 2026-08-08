import re
import pathlib
from collections import defaultdict

root = pathlib.Path("board/src/main/java/com/mynetpcb/board")
repo = pathlib.Path(".")

# Preload all java files content
java_files = list(repo.rglob("*.java"))
# exclude our temp script path if any
file_texts = {}
for p in java_files:
    try:
        file_texts[p] = p.read_text(encoding="utf-8", errors="ignore")
    except Exception:
        pass

overrides = {
    "toString", "hashCode", "equals", "clone", "compareTo", "paint", "print",
    "actionPerformed", "keyPressed", "keyReleased", "keyTyped",
    "mousePressed", "mouseReleased", "mouseClicked", "mouseDragged", "mouseMoved",
    "mouseEntered", "mouseExited", "mouseWheelMoved", "stateChanged", "valueChanged",
    "windowOpened", "windowClosing", "windowClosed", "windowIconified", "windowDeiconified",
    "windowActivated", "windowDeactivated", "updateUI", "fromXML", "toXML", "getState",
    "setState", "clear", "add", "move", "rotate", "mirror", "getBoundingShape", "isClicked",
    "isInRect", "getCenter", "setSelected", "isSelected", "getDrawingLayerPriority",
    "getClickableOrder", "drawClearance", "printClearance", "alignToGrid", "getNetShapes",
    "drawControlShape", "prepareClippingRegion", "getClippingRegion", "isVisibleOnLayers",
    "resize", "setResizingPoint", "getResizingPoint", "alignResizingPointToGrid",
    "isControlRectClicked", "isShapeDeletable", "isFloating", "isEndPoint", "deleteLastPoint",
    "shiftFloatingPoints", "getFloatingStartPoint", "getFloatingMidPoint", "getFloatingEndPoint",
    "reset", "insertPoint", "getLinePoints", "getPads", "getShapes", "setSide", "getSide",
    "setClearance", "getClearance", "getNetName", "setNetName", "getPadConnection",
    "setPadConnection", "getInner", "getOuter", "getTextureByTag", "getClickedTexture",
    "isClickedTexture", "createShape", "createUnitContainer", "resolve", "initDialogContent",
    "createPagePanel", "createContext", "selectShapeEvent", "deleteShapeEvent",
    "renameShapeEvent", "addShapeEvent", "propertyChangeEvent", "addUnitEvent",
    "deleteUnitEvent", "selectUnitEvent", "selectContainerEvent", "renameContainerEvent",
    "deleteContainerEvent", "renameUnitEvent", "createBlockMenuItems",
    "createLineSelectMenuItems", "createTrackMenuItems", "createChipMenuItems",
    "registerTrackPopup", "registerChipPopup", "registerUnitPopup", "getPopupMenu",
    "getResult", "loadStateTo", "saveStateFrom", "isSameState", "getLayoutPanel",
    "getPinPoints", "getPinsRect", "setGridUnits", "getGridUnits", "setGridValue",
    "getGridValue", "isStartAnglePointClicked", "isExtendAnglePointClicked",
    "isMidPointClicked", "getArcType", "mouseScaledPressed", "mouseScaledReleased",
    "mouseScaledDragged", "mouseScaledMove", "doubleScaledClick", "getEventHandle",
    "registerEventHandle", "getTargetEventHandle",
}

method_re = re.compile(
    r"(?m)^[ \t]*(public|protected|private)\s+(static\s+)?(final\s+)?"
    r"([\w.<>,\[\]\s]+?)\s+(\w+)\s*\(([^)]*)\)\s*(\{|throws|;)"
)

# Also package-private methods (no access modifier)
pp_re = re.compile(
    r"(?m)^[ \t]*(?!public\b|protected\b|private\b|static\b|class\b|interface\b|enum\b|"
    r"if\b|for\b|while\b|switch\b|return\b|new\b|try\b|catch\b|finally\b|else\b|@"
    r"|abstract\b|final\b|synchronized\b|native\b|default\b|void\b)"
    r"([\w.<>,\[\]]+(?:\s*<[^;{]+>)?)\s+(\w+)\s*\(([^)]*)\)\s*(\{|throws)"
)

methods = []
for p in sorted(root.rglob("*.java")):
    text = file_texts.get(p) or p.read_text(encoding="utf-8", errors="ignore")
    text_nc = re.sub(r"/\*.*?\*/", "", text, flags=re.S)
    text_nc = re.sub(r"//.*?$", "", text_nc, flags=re.M)
    classes = list(re.finditer(
        r"(?m)^[ \t]*(public\s+|protected\s+|private\s+)?(static\s+)?(abstract\s+|final\s+)?"
        r"(class|interface|enum)\s+(\w+)",
        text_nc,
    ))
    if not classes:
        continue
    # map line offsets to enclosing class roughly by position
    for m in method_re.finditer(text_nc):
        name = m.group(5)
        ret = m.group(4).strip()
        params = m.group(6).strip()
        vis = m.group(1)
        static = bool(m.group(2))
        # find nearest preceding class
        enclosing = None
        for c in classes:
            if c.start() < m.start():
                enclosing = c.group(5)
            else:
                break
        if enclosing is None:
            continue
        if name == enclosing:
            continue  # constructor
        methods.append({
            "cls": enclosing,
            "name": name,
            "ret": ret,
            "params": params,
            "vis": vis,
            "static": static,
            "file": str(p).replace("\\", "/"),
            "pos": m.start(),
        })

# Count references for each method name across repo (excluding defining file occurrences for call detection)
print("=== METHOD USAGE SCAN (non-override public/protected) ===")
likely_unused = []
ambiguous = []
used_count = 0

for item in methods:
    if item["vis"] == "private":
        continue
    name = item["name"]
    cls = item["cls"]
    # skip obvious overrides / listener / framework
    if name in overrides:
        continue
    # search call sites: .name( or Class.name( or name(
    pattern = re.compile(rf"\b{re.escape(name)}\s*\(")
    hits = []
    for fp, content in file_texts.items():
        # skip definition-only by allowing same file if multiple occurrences
        for hm in pattern.finditer(content):
            # skip import lines
            line_start = content.rfind("\n", 0, hm.start()) + 1
            line = content[line_start:content.find("\n", hm.start())]
            if line.strip().startswith("import "):
                continue
            hits.append((str(fp).replace("\\", "/"), line.strip()[:160]))

    # Filter definition itself: method signature lines containing return type + name(
    def_pat = re.compile(
        rf"(public|protected|private)\s+(static\s+)?(final\s+)?"
        rf"[\w.<>,\[\]\s]+\s+{re.escape(name)}\s*\("
    )
    external = []
    same_class_calls = []
    for fp, line in hits:
        if def_pat.search(line) and item["file"] in fp.replace("\\", "/"):
            continue
        if "abstract " in line and name + "(" in line and "{" not in line:
            # interface/abstract declaration elsewhere - still usage if override contract
            pass
        if item["file"] in fp.replace("\\", "/"):
            # same file - could be self call
            if not def_pat.search(line):
                same_class_calls.append((fp, line))
            continue
        external.append((fp, line))

    total_use = len(external) + len(same_class_calls)
    tag = ""
    if total_use == 0:
        tag = "UNUSED?"
        likely_unused.append(item)
    elif len(external) == 0 and same_class_calls:
        tag = "SELF-ONLY"
        # still used
        used_count += 1
    else:
        tag = "USED"
        used_count += 1

    if tag in ("UNUSED?", "SELF-ONLY") or name.startswith(("get", "set", "is", "create", "open", "switch", "delete", "register")):
        print(f"[{tag}] {cls}.{name}({item['params']}) vis={item['vis']} file={item['file']}")
        if external[:3]:
            for e in external[:3]:
                print(f"    EXT: {e[0]} :: {e[1]}")
        elif same_class_calls[:2]:
            for e in same_class_calls[:2]:
                print(f"    SELF: {e[0]} :: {e[1]}")

print(f"\nPublic/protected non-override likely unused: {len(likely_unused)}")
print(f"Used (among scanned non-overrides): {used_count}")

print("\n=== DETAILED LIKELY UNUSED ===")
for item in likely_unused:
    print(f"- {item['cls']}.{item['name']}({item['params']}) [{item['vis']}] {item['file']}")
