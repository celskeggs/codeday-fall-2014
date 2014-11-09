import sys, pygame, interpreter, client
pygame.init()

size = 1024, 600
screen = pygame.display.set_mode(size)
font = pygame.font.SysFont('monospace', 26)
backgroundAlive = pygame.Surface(screen.get_size())
backgroundAlive = backgroundAlive.convert()
backgroundAlive.fill((0, 0, 0))
backgroundDead = pygame.Surface(screen.get_size())
backgroundDead = backgroundDead.convert()
backgroundDead.fill((64, 0, 0))
green = (0, 255, 0)
red = (255, 0, 0)
blue = (0, 0, 255)
white = (255, 255, 255)
yellow = (255, 255, 0)
orange = (255, 127, 0)
color = [green, red, blue, white, yellow]

display_welcome = True

pygame.display.set_caption('Do that and Die')

welcome = font.render("Fight your friends. Kill the boss.", 0, green)
welcomepos = welcome.get_rect()
welcomepos.centerx = screen.get_rect().centerx
welcomepos.centery = screen.get_rect().centery

beta = font.render("Beta 1", 0, yellow)
betapos = beta.get_rect()
betapos.centerx = screen.get_rect().centerx
betapos.centery = screen.get_rect().centery + font.get_height()

font = pygame.font.SysFont('monospace', 16)

status_lobby = font.render("LOBBY", 0, green)
status_not = font.render("BATTLE", 0, green)

text_input = ""

while 1:
  countdown = font.render("Time Left in Battle: " + str(interpreter.client.dictionary.get("mode.countdown", 0)/100), 0, red)

  is_in_lobby = client.dictionary.get("mode.isinlobby", True)
  receive_line = interpreter.client.nextline()
  if receive_line != None:
    interpreter.lines.append(receive_line)

  for event in pygame.event.get():
    if event.type == pygame.KEYDOWN:
      display_welcome = False
      if event.key == pygame.K_RETURN:
        interpreter.send(text_input)
        text_input = ""
      elif event.key == pygame.K_BACKSPACE:
        text_input = text_input[:-1]
      elif event.key == pygame.K_ESCAPE:
        interpreter.client.close()
        sys.exit()
      else:
        text_input = text_input + event.unicode

    if event.type == pygame.QUIT:
      interpreter.client.close()
      sys.exit()

  text = font.render("> " + text_input, 0, green)
  screen.blit(backgroundDead if not interpreter.client.dictionary["mode.isinlobby"] and interpreter.client.my("isdead", False) else backgroundAlive, (0, 0))
  screen.blit(text, (5, size[1] - font.get_height()))

  if is_in_lobby:
    screen.blit(status_lobby, (size[0] - status_lobby.get_width() - 10, 0))
  else:
    screen.blit(status_not, (size[0] - status_not.get_width() - 10, 0))
    screen.blit(countdown, (size[0] - countdown.get_width() - 10, (screen.get_rect().centery + font.get_height() * 4)))

  for i in [0, 1, 2, 3, 4]:
    n = "boss" if i == 4 else str(i)
    if interpreter.client.dictionary.get("connected." + n, False) or n == "boss":
      player_list = font.render(interpreter.client.dictionary.get("name." + n, "None") + ": " +
                                interpreter.client.dictionary.get("class." + n, "No Class Picked")
                                , 0, color[i])
      health = interpreter.client.dictionary.get("health." + n, None)
      if health == None:
        health = "???"
      elif health <= 0:
        health = "*DEAD*"
      player_health = font.render("Health: " + str(health), 0, color[i])
      screen.blit(player_list, (size[0] - player_list.get_width() - 10,
                                               (screen.get_rect().centery - (font.get_height() * ((i * 2) - 1)))))
      screen.blit(player_health, (size[0] - player_health.get_width() - 10,
                                 (screen.get_rect().centery - font.get_height() * ((i * 2) - 2))))

  if client.my("status.bleed", 0) > 0:
    player_bleed = font.render("You are now bleeding", 0, red)
    screen.blit(player_bleed, ((screen.get_rect().centerx - (player_bleed.get_width()/2), 5)))
  if client.my("status.burn", 0) > 0:
    player_burn = font.render("You have been burnt", 0, orange)
    screen.blit(player_burn, ((screen.get_rect().centerx - (player_burn.get_width()/2)), (font.get_height() + 5)))
  if client.my("status.paralyze", 0) > 0:
    player_paralyze = font.render("You have been paralyzed", 0, yellow)
    screen.blit(player_paralyze, ((screen.get_rect().centerx - player_paralyze.get_width()/2), (font.get_height() * 2) + 5))
  if display_welcome == True:
    screen.blit(welcome, welcomepos)
    screen.blit(beta, betapos)
  for i, line in enumerate(interpreter.lines, 1):
    new_line = font.render(line, 0, green)
    screen.blit(new_line, (5, (size[1] - font.get_height()) -
                           (font.get_height() * (len(interpreter.lines) - i)) - font.get_height()))


  pygame.display.flip()



