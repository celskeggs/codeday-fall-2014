import sys, pygame, interpreter, client
pygame.init()

size = 1024, 600
screen = pygame.display.set_mode(size)
background = pygame.Surface(screen.get_size())
background = background.convert()
green = (0, 255, 0)
red = (255, 0, 0)
blue = (0, 0, 255)
white = (255, 255, 255)
color = [green, red, blue, white]

display_welcome = True

pygame.display.set_caption('Do that and Die')

background.fill((0, 0, 0))

font = pygame.font.SysFont('monospace', 18)
welcome = font.render("Welcome to this Text-based Game", 0, green)

textpos = welcome.get_rect()
textpos.centerx = background.get_rect().centerx
textpos.centery = background.get_rect().centery

status_lobby = font.render("Lobby", 0, green)
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

  text = font.render(text_input, 0, green)
  screen.blit(background, (0, 0))
  screen.blit(text, (5, size[1] - font.get_height()))

  if is_in_lobby == True:
    screen.blit(status_lobby, (size[0] - status_lobby.get_width(), 0))
  elif is_in_lobby == False:
    screen.blit(status_not, (size[0] - status_not.get_width(), 0))
    screen.blit(countdown, (size[0] - countdown.get_width(), (background.get_rect().centery - font.get_height() *4)))

  for i in range(0, 4):
    if interpreter.client.dictionary.get("connected." + str(i), False):
      player_list = font.render(interpreter.client.dictionary.get("name." + str(i), "None") + ": " +
                                interpreter.client.dictionary.get("class." + str(i), "No Class Picked")
                                , 0, color[i])
      player_health = font.render("Health: " + str(interpreter.client.dictionary.get("health." + str(i), "")), 0, color[i])
      screen.blit(player_list, (size[0] - player_list.get_width(),
                                               (background.get_rect().centery - (font.get_height() * ((i * 2) - 1)))))
      screen.blit(player_health, (size[0] - player_health.get_width(),
                                 (background.get_rect().centery - font.get_height() * ((i * 2) - 2))))

  if display_welcome == True:
    screen.blit(welcome, textpos)

  for i, line in enumerate(interpreter.lines, 1):
    new_line = font.render(line, 0, green)
    screen.blit(new_line, (5, (size[1] - font.get_height()) -
                           (font.get_height() * (len(interpreter.lines) - i)) - font.get_height()))


  pygame.display.flip()



