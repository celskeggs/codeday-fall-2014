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
      elif client.my("class", None) == class_name:
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
  elif words[0].lower() == "wait":
    client.send("wait", None)
  elif client.my("class", None) != None and words[0].lower() == ("commands"):
    for command in commands[client.my("class")]:
      lines.append(command)
    lines.append("wait")
  elif words[0].lower() == "help":
    lines.append("")
    lines.append("")
    lines.append("")
    lines.append("YELLOW~------------------------------")
    lines.append("YELLOW~Choose your name")
    lines.append("YELLOW~Type '~RED~name~YELLOW~' then your choice of name")
    lines.append("YELLOW~Choose your class")
    lines.append("YELLOW~You may pick between a ~RED~Wizard~YELLOW~, ~RED~Ranger~YELLOW~, ~RED~Soldier~YELLOW~, or ~RED~Robot")
    lines.append("YELLOW~Choose by typing '~RED~class~YELLOW~' then your choice")
    lines.append("YELLOW~Try to be the last man standing")
    lines.append("YELLOW~Use your moves to injure your enemies")
    lines.append("YELLOW~See your list of moves by typing '~RED~commands~YELLOW~'")
    lines.append("YELLOW~To attack, type your move and the specified target, either '~RED~boss~YELLOW~' or a player's name")
    lines.append("YELLOW~To chat, type '~RED~say~YELLOW~' then your message")
    lines.append("YELLOW~Start the game by typing '~RED~enter~YELLOW~'")
    lines.append("YELLOW~Have fun")
    lines.append("YELLOW~------------------------------")
    lines.append("")
    lines.append("")
    lines.append("")
  else:
    lines.append("------Unrecognizable Command------")
