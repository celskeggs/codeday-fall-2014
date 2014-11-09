import sys, pygame, interpreter
pygame.init()

size = 640, 480
screen = pygame.display.set_mode(size)
background = pygame.Surface(screen.get_size())
background = background.convert()
green = (0, 255, 0)

display_welcome = True

pygame.display.set_caption('Do that and Die')

background.fill((0, 0, 0))

font = pygame.font.SysFont('monospace', 18)
welcome = font.render("Welcome to this Text-based Game", 0, green)
textpos = welcome.get_rect()
textpos.centerx = background.get_rect().centerx
textpos.centery = background.get_rect().centery

text_input = ""
y = 420

while 1:

  receive_line = interpreter.client.nextline()
  if receive_line != None:
    interpreter.lines.append(line)

  for event in pygame.event.get():
    if event.type == pygame.KEYDOWN:
      display_welcome = False
      if event.key == pygame.K_RETURN:
        interpreter.send(text_input)
        text_input = ""
      elif event.key == pygame.K_BACKSPACE:
        text_input = text_input[:-1]
      else:
        text_input = text_input + event.unicode
    if event.type == pygame.QUIT: sys.exit()
  text = font.render(text_input, 0, green)
  screen.blit(background, (0, 0))
  screen.blit(text, (5, 480 - font.get_height()))
  if display_welcome == True:
    screen.blit(welcome, textpos)

  for i, line in enumerate(interpreter.lines, 1):
    new_line = font.render(line, 0, green)
    screen.blit(new_line, (5, (480 - font.get_height()) -
                           (font.get_height() * (len(interpreter.lines) - i)) - font.get_height()))


  pygame.display.flip()





















