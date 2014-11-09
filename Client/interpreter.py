import client, sys
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
    lines.append("BLUE~------------------------------")
    lines.append("BLUE~Choose your name")
    lines.append("BLUE~Type '~GREEN~name~BLUE~' then your choice of name")
    lines.append("BLUE~Choose your class")
    lines.append("BLUE~You may pick between a Wizard, Ranger, Soldier, and Robot")
    lines.append("BLUE~Choose by typing '~GREEN~class~BLUE~' then your choice")
    lines.append("BLUE~Try to be the last man standing")
    lines.append("BLUE~Use your moves to injure your enemies")
    lines.append("BLUE~See your list of moves by typing '~GREEN~commands~BLUE~'")
    lines.append("BLUE~To attack, type your move and the specified target, either '~GREEN~boss~BLUE~' or a player's name")
    lines.append("BLUE~To chat, type '~GREEN~say~BLUE~' then your message")
    lines.append("BLUE~Start the game by typing '~GREEN~enter~BLUE~'")
    lines.append("BLUE~Have fun")
    lines.append("BLUE~------------------------------")
    lines.append("")
    lines.append("")
    lines.append("")
  else:
    lines.append("------Unrecognizable Command------")
