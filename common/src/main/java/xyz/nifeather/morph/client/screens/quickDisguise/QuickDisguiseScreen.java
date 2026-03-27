package xyz.nifeather.morph.client.screens.quickDisguise;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import xyz.nifeather.morph.client.ClientMorphManager;
import xyz.nifeather.morph.client.EntityCache;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;
import xyz.nifeather.morph.client.graphics.*;
import xyz.nifeather.morph.client.graphics.container.DrawableButtonWrapper;
import xyz.nifeather.morph.client.graphics.container.FlowContainer;
import xyz.nifeather.morph.client.graphics.container.TextFieldWidgetWrapper;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.ClientPropertyHolder;
import xyz.nifeather.morph.client.properties.ClientDisguiseProperties;
import xyz.nifeather.morph.client.screens.FeatherScreen;
import xyz.nifeather.morph.client.storage.struct.SavedDisguise;
import xyz.nifeather.morph.client.syncers.OtherClientDisguiseSyncer;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class QuickDisguiseScreen extends FeatherScreen
{
    private final FeatherMorphClientBootstrap morphClient;

    private final ClientMorphManager morphManager;

    @Nullable
    private OtherClientDisguiseSyncer syncer;

    @Nullable
    private EntityDisplay currentDisplay;

    @Nullable
    private SavedDisguise selectedDisguise;

    @NotNull
    private WidgetList selectionList;
    private DrawableText selectionDisplayText;

    private final Map<String, SavedDisguise> availableDisguises = new ConcurrentHashMap<>();
    private TextFieldWidgetWrapper searchField;

    public Optional<SavedDisguise> selectedDisguise()
    {
        return Optional.ofNullable(selectedDisguise);
    }

    public QuickDisguiseScreen()
    {
        super(Component.literal("Quick disguise configuration screen"));

        this.morphClient = Objects.requireNonNull(FeatherMorphClientBootstrap.getInstance());
        this.morphManager = morphClient.morphManager;
        var savedDisguies = morphClient.savedDisguiseStorage;

        for (String name : savedDisguies.listAll())
        {
            var saved = savedDisguies.get(name);
            if (saved == null) continue;

            availableDisguises.put(name, saved);
        }
    }

    @Override
    protected void onScreenEnter(@Nullable Screen lastScreen)
    {
        super.onScreenEnter(lastScreen);

        var topBackground = new DrawableSprite(Screen.MENU_BACKGROUND, false);
        topBackground.setRelativeSizeAxes(Axes.X);
        topBackground.setHeight(getHeaderHeight());
        topBackground.setWidth(1);
        topBackground.textureHeight = 32;
        topBackground.textureWidth = 32;
        add(topBackground);

        var bottomBackground = new DrawableSprite(Screen.MENU_BACKGROUND, false);
        bottomBackground.setRelativeSizeAxes(Axes.X);
        bottomBackground.setHeight(getFooterHeight());
        bottomBackground.setAnchor(Anchor.BottomLeft);
        bottomBackground.setWidth(1);
        bottomBackground.textureHeight = 32;
        bottomBackground.textureWidth = 32;
        add(bottomBackground);

        var list = new WidgetList(minecraft, 0, 0, 0 ,22);
        list.setSize(new Vector2f(this.width * 0.6f, this.height - 100));
        list.onSelect = this::onSelect;
        this.selectionList = list;

        updateSelectionListSize();
        availableDisguises.forEach(list::addEntry);

        add(list);

        var closeButton = new DrawableButtonWrapper(Button.builder(Component.translatable("gui.back"), clicked ->
        {
            this.onClose();
        }).width(80).build());

        var buttonContainer = new FlowContainer();
        buttonContainer.setFlowAxes(Axes.X);
        buttonContainer.setSpacing(5);
        buttonContainer.addRange(closeButton);
        buttonContainer.setAnchor(Anchor.BottomRight);
        buttonContainer.setSize(new Vector2f(80, 20));
        buttonContainer.setMargin(new MarginPadding(5));
        add(buttonContainer);

        var title = new DrawableText(Component.translatable("gui.morphclient.saved_disguise_selection"));
        title.setMargin(new MarginPadding(15, 5, 5, 5));
        add(title);

        var subTitle = new DrawableText(Component.translatable("gui.morphclient.double_click_to_disguise"));
        subTitle.setMargin(new MarginPadding(15, 0, 5 + font.lineHeight + 2, 0));
        add(subTitle);

        var selected = new DrawableText("");
        selected.setMargin(new MarginPadding(5));
        selected.setAnchor(Anchor.BottomRight);
        selected.setY(-getFooterHeight());
        selected.setBackgroundColor(0x80333333);
        selected.setShadowExpandPixels(2);
        this.selectionDisplayText = selected;
        add(selected);

        var searchTextField = new TextFieldWidgetWrapper(
                new EditBox(font, 100, 20, Component.literal("Type to search..."))
        );
        searchTextField.setChangedListener(this::applySearch);
        searchTextField.setAnchor(Anchor.TopRight);
        searchTextField.setMargin(new MarginPadding(5));
        searchTextField.setX(-25);
        add(searchTextField);
        this.searchField = searchTextField;

        onSelect("None", null);

        var topHeader = new DrawableSprite(Screen.INWORLD_HEADER_SEPARATOR, false);
        topHeader.setY(getHeaderHeight());
        topHeader.setSize(new Vector2f(1, 2));
        topHeader.setRelativeSizeAxes(Axes.X);
        add(topHeader);

        var bottomFooter = new DrawableSprite(Screen.INWORLD_FOOTER_SEPARATOR, false);
        bottomFooter.setAnchor(Anchor.BottomLeft);
        bottomFooter.setY(-getFooterHeight());
        bottomFooter.setSize(new Vector2f(1, 2));
        bottomFooter.setRelativeSizeAxes(Axes.X);
        add(bottomFooter);
    }

    private int getHeaderHeight()
    {
        return 30;
    }

    private int getFooterHeight()
    {
        return 30;
    }

    private int getContentHeight()
    {
        return this.height - getHeaderHeight() - getFooterHeight();
    }

    @Override
    protected void init()
    {
        super.init();
        this.setFocused(searchField);
    }

    @Override
    protected void onScreenResize()
    {
        updateSelectionListSize();
/*
        int XonDisplay = Math.round(this.width * 0.6f / 2);
        subTitle.setX(XonDisplay);
        subTitle.setY(selectionList.getY() - Math.round(subTitle.getRenderHeight()));*/

        super.onScreenResize();
    }

    private void updateSelectionListSize()
    {
        selectionList.setWidth(Math.round(this.width * 0.6f));
        selectionList.setHeight(getContentHeight() - 4);
        selectionList.setY(getHeaderHeight() + 2);
    }

    private void onConfirm()
    {
        this.selectedDisguise().ifPresent(saved ->
        {
            morphClient.requestDisguise(saved.disguiseIdentifier(), saved.properties());
            this.onClose();
        });
    }

    @Override
    public void tick()
    {
        var syncer = this.syncer;
        if (syncer != null)
            syncer.onGameTick();

        super.tick();
    }

    public void applySearch(String input)
    {
        selectionList.clear();
        selectedDisguise = null;

        if (input.isBlank())
        {
            availableDisguises.forEach(selectionList::addEntry);
            return;
        }

        availableDisguises.entrySet()
                .stream().filter(entry -> entry.getKey().contains(input))
                .forEach(entry -> selectionList.addEntry(entry.getKey(), entry.getValue()));
    }

    private void onSelect(String name, @Nullable SavedDisguise saved)
    {
        if (saved != null && selectedDisguise == saved)
        {
            onConfirm();
            return;
        }

        selectionDisplayText.setText(Component.literal("..."));

        disposeDisplay();
        this.selectedDisguise = saved;

        var id = saved == null ? FeatherMorphClientBootstrap.UNMORPH_STIRNG : saved.disguiseIdentifier();
        var nextDisplay = new LocalEntityDisplay(id, true, EntityDisplay.InitialSetupMethod.ASYNC, localCache);
        nextDisplay.setAnchor(Anchor.CentreRight);
        nextDisplay.setRelativeSizeAxes(Axes.Both);
        nextDisplay.setSize(new Vector2f(0.4f, 0.7f));
        nextDisplay.postEntitySetup = () ->
        {
            if (saved == null) return;

            var entity = nextDisplay.getDisplayingEntity();
            if (entity == null)
                return;

            minecraft.level.addEntity(entity);

            var properties = saved.properties();

            var holder = new ClientPropertyHolder();
            var propertyCollection = ClientDisguiseProperties.INSTANCE.getHandler(entity).orElseThrow();
            holder.registerFromPropertyCollection(propertyCollection);

            this.syncer = new OtherClientDisguiseSyncer(minecraft.player, saved.disguiseIdentifier(), -1, entity);
            this.syncer.propertyHolder().registerFromPropertyCollection(propertyCollection);
            this.syncer.propertyHolder().updateFromPropertiesInput(properties).forEach((p, v) ->
            {
                ((ClientProperty<Object, Entity>) p).apply(entity, v);
            });

            selectionDisplayText.setText(entity.getDisplayName());
        };

        currentDisplay = nextDisplay;
        add(nextDisplay);
    }

    private final EntityCache localCache = new EntityCache();

    public void disposeDisplay()
    {
        if (currentDisplay != null)
        {
            currentDisplay.dispose();
            this.remove(currentDisplay);
            currentDisplay = null;
        }

        if (syncer != null)
        {
            syncer.dispose();
            syncer = null;
        }
    }

    @Override
    public void removed()
    {
        localCache.dispose();
        disposeDisplay();

        super.removed();
    }

    private static class LocalEntityDisplay extends EntityDisplay
    {
        private final EntityCache entityCache;

        public LocalEntityDisplay(@NotNull String rawIdentifier,
                                  boolean displayLoadingIfNotValid,
                                  InitialSetupMethod initialSetupMethod,
                                  EntityCache entityCache)
        {
            super(rawIdentifier, displayLoadingIfNotValid, initialSetupMethod);

            this.entityCache = entityCache;
        }

        @Override
        protected void onRender(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta)
        {
            var yStart = (int)getScreenSpaceY();

            var yCentre = yStart + renderHeight / 2;
            var mX = getScreenSpaceX() + renderWidth * 0.3f;

            super.onRender(context, (int)mX, yCentre, delta);
        }

        @Override
        protected EntityCache entityCache()
        {
            return this.entityCache;
        }
    }
}
