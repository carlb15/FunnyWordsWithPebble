/*
* @authors Carl Barbee, Jimmy Dagres
* @assignment Pebble app which receives a word and definition from
*             a text file from an Android app.
* @date April 21, 2014
*/
#include "pebble.h"

// Main window for app.
static Window *window;
// word and definition text layers
static TextLayer *word_layer, *definition_layer;
// buffers for the word and definition.
char word_buffer[64], definition_buffer[64];

/*The key words for each message.*/
enum {
  KEY_WORD = 0,
  KEY_DEFINITION = 1,
  BUTTON_EVENT_SELECT = 2,
  BUTTON_EVENT_UP = 3,
  BUTTON_EVENT_DOWN = 4,
  PEBBLE_PACKAGE = 5,
};

/* Set-ups TextLayer to specification provided in arguments.*/
static TextLayer* init_text_layer(GRect location, GColor colour, GColor background, const char *res_id, GTextAlignment alignment)
{
  TextLayer *layer = text_layer_create(location);
  text_layer_set_text_color(layer, colour);
  text_layer_set_background_color(layer, background);
  text_layer_set_font(layer, fonts_get_system_font(res_id));
  text_layer_set_text_alignment(layer, alignment);
 
  return layer;
}

/*Key and value of Tuple is read and key is used to 
  decide what to do with the accompanying data. */
void process_tuple(Tuple *t)
{
  //Get key
  int key = t->key;
 
  //Get string value, if present
  char string_value[64];
  strcpy(string_value, t->value->cstring);
   
  //Decide what to do
  switch(key) {
    case KEY_WORD:
      //Word received
      snprintf(word_buffer, sizeof(word_buffer), "%s", string_value);
      text_layer_set_text(word_layer, (char*) &word_buffer);
      break;
    case KEY_DEFINITION:
      //Definition received
      snprintf(definition_buffer, sizeof(definition_buffer), "%s", string_value);
      text_layer_set_text(definition_layer, (char*) &definition_buffer);
      // Vibrate watch to indicate the message has been received.
      vibes_short_pulse();
      break;
  }
}

/* This function sends the key and its values to 
the android application */ 
void send_int(uint8_t key, uint8_t cmd)
{
    DictionaryIterator *iter;
    app_message_outbox_begin(&iter);
      
    Tuplet value = TupletInteger(key, cmd);
    dict_write_tuplet(iter, &value);
      
    app_message_outbox_send();
}

/* Processing the received Tuples*/
static void in_received_handler(DictionaryIterator *iter, void *context) 
{
  //Get first message.
  Tuple *t = dict_read_first(iter);
  
  if(t)
  {
    process_tuple(t);
  }
   
  //Get next message
  while(t != NULL)
  {
    t = dict_read_next(iter);
    if(t)
    {
      process_tuple(t);
    }
  }
}

static void select_click_handler(ClickRecognizerRef recognizer, void *context) {
  send_int(PEBBLE_PACKAGE, BUTTON_EVENT_SELECT);
}
 
static void up_click_handler(ClickRecognizerRef recognizer, void *context) {
  send_int(PEBBLE_PACKAGE, BUTTON_EVENT_UP);
}
 
static void down_click_handler(ClickRecognizerRef recognizer, void *context) {
  send_int(PEBBLE_PACKAGE, BUTTON_EVENT_DOWN);
}
 
static void click_config_provider(void *context) {
  window_single_click_subscribe(BUTTON_ID_SELECT, select_click_handler);
  window_single_click_subscribe(BUTTON_ID_UP, up_click_handler);
  window_single_click_subscribe(BUTTON_ID_DOWN, down_click_handler);
}
/* Load all window sub-elements */
static void window_load(Window *window) {
  // Setup title layer where each word will be placed.
  word_layer = init_text_layer(GRect(5, 0, 144, 30), GColorBlack, GColorClear, "RESOURCE_ID_GOTHIC_24_BOLD", GTextAlignmentLeft);
  text_layer_set_text(word_layer,"Welcome to Funny Words!");
  // Adds the text layer as a child of the window layer.  
  layer_add_child(window_get_root_layer(window), text_layer_get_layer(word_layer));
  // Setup definition text layer where the word's definition will be placed.
  definition_layer = init_text_layer(GRect(5, 60, 144, 30), GColorBlack, GColorClear, "RESOURCE_ID_GOTHIC_22", GTextAlignmentLeft);
  
  // Allign the texts
  text_layer_set_text_alignment(word_layer, GTextAlignmentCenter);
  text_layer_set_text_alignment(definition_layer, GTextAlignmentCenter);
  
  // Adds the text layer as a child of the window layer.  
  layer_add_child(window_get_root_layer(window), text_layer_get_layer(definition_layer));
}

/* Un-load all window sub-elements */
static void window_unload(Window *window) {
  text_layer_destroy(word_layer);
  text_layer_destroy(definition_layer);
}

/* Initialize the main app elements */
static void init() {
  // Initialize app elements
  window = window_create();
  window_set_click_config_provider(window, click_config_provider);
  window_set_window_handlers(window, (WindowHandlers) {
    .load = window_load,
    .unload = window_unload
  });

  // Register AppMessage Events
  app_message_register_inbox_received(in_received_handler);           
  app_message_open(512, 512);    //Large input and output buffer sizes
  
  // Window will slide into view.
  const bool animated = true;
  window_stack_push(window, animated);
}

/* De-initialize the main app elements */
static void deinit() {
 //De-initialize elements here to save memory!
   window_destroy(window);
}

/* Main app lifecycle */
int main(void) {
  init();
  app_event_loop();
  deinit();
}