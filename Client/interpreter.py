import client
lines = ['']
commands = {"wizard": ["burn", "grind", "drown", "blast", "zap"], "soldier": ["shoot", "bombard", "punch", "kick", "stun"], "ranger": ["draw", "shank", "slash", "throw", "kick"], "robot": ["pew", "pewpew", "pewpewpew", "pewpewpewpew", "pow"]}

wizard_commands = ['burn', "grind", "drown", "blast", "zap"]
soldier_commands = ["shoot", "bombard", "punch", "kick", "stun"]
ranger_commands = ["draw", "shank", "slash", "throw", "kick"]
robot_commands = ["pew", "pewpew", "pewpewpew", "pewpewpewpew", "pow"]


def send(line):
  print line
  lines.append(line)
  words = line.split()
  for class_name, class_commands in commands.items():
    if words[0].lower() in class_commands:
      if len(words) < 2:
        lines.append("You need to specify a target")
      elif client.dictionary["class.0"] == class_name:
          client.send("attack", (words[0], words[1]))

      else:
          lines.append("Your class cannot use that command")
      return
  if words[0].lower() == "hello":
    client.send("hello", 20)
  elif words[0].lower() == "enter":
    client.send("enter", None)
  else:
    lines.append("------Unrecognizable Command------")
