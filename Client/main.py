import sys, pygame, interpreter, client, os
pygame.init()

x = 100
y = 50

os.environ['SDL_VIDEO_WINDOW_POS'] = "%d, %d" % (x, y)

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
purple = (255, 0, 255)
color = [green, red, blue, white, yellow]

display_welcome = True

pygame.display.set_caption("DON'T DO THAT")

welcome = font.render("fight your friends. kill the boss.", 0, green)
welcomepos = welcome.get_rect()
welcomepos.centerx = screen.get_rect().centerx
welcomepos.centery = screen.get_rect().centery

beta = font.render("beta 3", 0, yellow)
betapos = beta.get_rect()
betapos.centerx = screen.get_rect().centerx
betapos.centery = screen.get_rect().centery + font.get_height()

instructions = font.render("type 'help' for instructions", 0, red)
instructionspos = instructions.get_rect()
instructionspos.centerx = screen.get_rect().centerx
instructionspos.centery = screen.get_rect().centery + font.get_height() + font.get_height()

font = pygame.font.SysFont('monospace', 16)

text_input = ""

color_lookup = {"RED": red, "GREEN": green, "BLUE": blue, "WHITE": white, "YELLOW": yellow, "ORANGE": orange, "PURPLE": purple}
LEFT, CENTER, RIGHT = -1, 0, 1
def draw_colorable_text(text, color, x, y, align=CENTER):
    parts = str(text).split("~")
    rparts = [part for part in parts if part not in color_lookup]
    if align == CENTER:
        x -= font.size("".join(rparts))[0] / 2
    elif align == RIGHT:
        x -= font.size("".join(rparts))[0]
    for part in parts:
        if part in color_lookup:
            color = color_lookup[part]
        else:
            comp = font.render(part, 0, color)
            screen.blit(comp, (x, y))
            x += comp.get_width()

while 1:
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

  screen.blit(backgroundDead if not is_in_lobby and interpreter.client.my("isdead", False) else backgroundAlive, (0, 0))
  draw_colorable_text("> " + text_input, green, 5, size[1] - font.get_height(), LEFT)

  if is_in_lobby:
    draw_colorable_text("LOBBY", green, size[0] - 10, 0, RIGHT)
  else:
    draw_colorable_text("BATTLE", green, size[0] - 10, 0, RIGHT)
    draw_colorable_text("Time Left in Battle: " + str(interpreter.client.dictionary.get("mode.countdown", 0)/100), red, size[0] - 10, screen.get_rect().centery + font.get_height() * 4, RIGHT)

  for i in [0, 1, 2, 3, 4]:
    n = "boss" if i == 4 else str(i)
    if i != 4:
      draw_colorable_text("Level: " + str(interpreter.client.dictionary.get("level." + interpreter.client.dictionary.get("ip." + str(i), ""), 1)), color[i], size[0] - 10, screen.get_rect().centery - font.get_height() * ((i * 3) - 3), RIGHT)
    if interpreter.client.dictionary.get("connected." + n, False) or n == "boss":
      health = interpreter.client.dictionary.get("health." + n, None)
      if health == None:
        health = "???"
      elif health <= 0:
        health = "***DEAD***"
      else:
        health = str(health)
      if interpreter.client.dictionary.get("status.bleed." + n, 0) > 0:
        health += " [BLEEDING]"
      if interpreter.client.dictionary.get("status.burn." + n, 0) > 0:
        health += " [BURNING]"
      if interpreter.client.dictionary.get("status.paralyze." + n, 0) > 0:
        health += " [PARALYZED]"
      if interpreter.client.dictionary.get("isready." + n, False) and interpreter.client.dictionary["mode.isinlobby"]:
        health += " [READY]"
      draw_colorable_text(interpreter.client.dictionary.get("name." + n, "none") + ": " + interpreter.client.dictionary.get("class." + n, "no class picked"), color[i], size[0] - 10, screen.get_rect().centery - (font.get_height() * ((i * 3) - 1)), RIGHT)
      draw_colorable_text("Health: " + str(health), color[i], size[0] - 10, screen.get_rect().centery - font.get_height() * ((i * 3) - 2), RIGHT)

  if client.my("status.bleed", 0) > 0:
    draw_colorable_text("YOU ARE NOW BLEEDING", red, screen.get_rect().centerx, 5, CENTER)
  if client.my("status.burn", 0) > 0:
    draw_colorable_text("YOU HAVE BEEN BURNT", orange, screen.get_rect().centerx, (font.get_height()) + 5, CENTER)
  if client.my("status.paralyze", 0) > 0:
    draw_colorable_text("YOU HAVE BEEN PARALYZED", yellow, screen.get_rect().centerx, (font.get_height() * 2) + 5, CENTER)
  if display_welcome == True:
    screen.blit(welcome, welcomepos)
    screen.blit(beta, betapos)
    screen.blit(instructions, instructionspos)
  for i, line in enumerate(interpreter.lines, 1):
    draw_colorable_text(line, green, 5, (size[1] - font.get_height()) - (font.get_height() * (len(interpreter.lines) - i)) - font.get_height(), LEFT)
  pygame.display.flip()
