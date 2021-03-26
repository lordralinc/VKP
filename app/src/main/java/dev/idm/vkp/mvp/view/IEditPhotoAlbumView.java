package dev.idm.vkp.mvp.view;

import dev.idm.vkp.mvp.view.base.IAccountDependencyView;
import dev.idm.vkp.mvp.view.base.ISteppersView;
import dev.idm.vkp.view.steppers.impl.CreatePhotoAlbumStepsHost;

public interface IEditPhotoAlbumView extends IAccountDependencyView, ISteppersView<CreatePhotoAlbumStepsHost>, IErrorView {

}
