import client
lines = ['']

wizard_commands = ['burn', "grind", "drown", "blast", "zap"]
soldier_commands = ["shoot", "bombard", "punch", "kick", "stun"]
ranger_commands = ["draw", "shank", "slash", "throw", "kick"]
robot_commands = ["pew", "pewpew", "pewpewpew", "pewpewpewpew", "pow"]


def send(line): # the player typed a message
  print line
  lines.append(line)
  words = line.split()
  if words[0].lower() == "burn":

  elif words[0].lower() == "grind":

  elif words[0].lower() == "drown":

  elif words[0].lower() == "blast":

  elif words[0].lower() == "zap":

  else:
    lines.append("What?")
