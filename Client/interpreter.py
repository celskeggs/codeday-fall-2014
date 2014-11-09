import client
lines = []
commands = {"wizard": ["burn", "grind", "drown", "blast", "zap"], "soldier": ["shoot", "bombard", "punch", "kick", "stun"], "ranger": ["draw", "shank", "slash", "throw", "kick"], "robot": ["pew", "pewpew", "inhale", "cook", "burn"]}

def send(line):
  print line
  words = line.split()
  if not words:
    return
  lines.append("$ " + line)
  was_byp = False
  was_found = False
  for class_name, class_commands in commands.items():
    if words[0].lower() in class_commands:
      if len(words) < 2:
        lines.append("You need to specify a target")
      elif client.my("class") == class_name:
          client.send("attack", (words[0], words[1]))
      else:
        was_byp = True
        continue
      was_found = True
      return
  if was_byp and not was_found:
    lines.append("Your class can't do that.")
  elif words[0].lower() == "enter":
    client.send("enter", None)
  elif words[0].lower() == "name":
    if len(words) < 2:
      lines.append("You need to specify a username as a second parameter")
    else:
      username = words[1]
      client.send("name", username)
  elif words[0].lower() == "say":
    if len(words) < 2:
      lines.append("Sure, what are you going to say?")
    else:
      client.send("chat", " ".join(words[1:]))
  elif words[0].lower() == "class":
    if len(words) < 2:
      lines.append("You need to specify a class as a second parameter")
    else:
      classname = words[1]
      client.send("class", classname.lower())
  elif words[0].lower() == "exit":
    sys.exit()
  elif client.my("class", None) != None and words[0].lower() == ("commands"):
    for command in commands[client.my("class")]:
      lines.append(command)
  elif words[0].lower() == "help":
    lines.append("")
    lines.append("")
    lines.append("")
    lines.append("------------------------------")
    lines.append("Choose your name")
    lines.append("Type 'name' then your choice of name")
    lines.append("Choose your class")
    lines.append("You may pick between a Wizard, Ranger, Soldier, and Robot")
    lines.append("Choose by typing 'class' then your choice")
    lines.append("Try to be the last man standing")
    lines.append("Use your moves to injure your enemies")
    lines.append("See your list of moves by typing 'commands'")
    lines.append("To attack, type your move and the specified target, either 'boss' or a player's name")
    lines.append("To chat, type 'say' then your message")
    lines.append("Start the game by typing 'enter'")
    lines.append("Have fun")
    lines.append("------------------------------")
    lines.append("")
    lines.append("")
    lines.append("")
  else:
    lines.append("------Unrecognizable Command------")
